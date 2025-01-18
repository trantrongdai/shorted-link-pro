# 1. Define cloud provider
provider "google" {
  credentials = file("/Users/tony/Documents/study/gcp/iac/key/cohesive-bolt-446213-k2-0270c8d17958.json")
  project     = "cohesive-bolt-446213-k2"
  region      = "asia-southeast1"
}

#============== START OUTPUT ===========================================================
output "static_ips" {
  value = google_compute_address.static_ip[*].address
}

output "jenkin_static_ip" {
  value = google_compute_address.jenkin_static_ip.address
}
#============== END OUTPUT ===========================================================

#============== START NETWORK =========================================================
# Create a VPC network for the instance
resource "google_compute_network" "vpc_network" {
  name = "shorted-link-network"
}

# Create a subnetwork
resource "google_compute_subnetwork" "vpc_subnetwork" {
  name          = "shorted-link-subnetwork"
  ip_cidr_range = "10.0.0.0/24"  # Adjust the CIDR range as necessary
  region      = "asia-southeast1"
  network       = google_compute_network.vpc_network.name
}

# Create a static external IP address (optional)
resource "google_compute_address" "static_ip" {
  count   = length(var.instance_names)  # Create a static IP for each instance
  name    = "${var.instance_names[count.index]}-static-ip"
  region      = "asia-southeast1" # Ensure this matches the provider region
  address_type = "EXTERNAL"   # Use "INTERNAL" if you need a private IP

  # Optional: Set the address field if you want a specific IP.
  # address = "192.0.2.1"      # Uncomment and specify if you want a specific static IP.
}

# Create a static external IP address for jenkin
resource "google_compute_address" "jenkin_static_ip" {
  name    = "jenkin-static-ip"
  region      = "asia-southeast1" # Ensure this matches the provider region
  address_type = "EXTERNAL"   # Use "INTERNAL" if you need a private IP
}

# Optional: Creating a firewall rule to allow HTTP traffic
resource "google_compute_firewall" "allow_http" {
  name    = "allow-http"
  network = google_compute_network.vpc_network.name

  allow {
    protocol = "tcp"
    ports    = ["0-65535"]  # Allow all TCP ports
  }

  allow {
    protocol = "udp"
    ports    = ["0-65535"]  # Allow all UDP ports
  }

  allow {
    protocol = "icmp"  # Allow ICMP traffic (for ping)
  }

  source_ranges = ["0.0.0.0/0"]  # Allow access from anywhere; consider restricting this
}
#============== END NETWORK =========================================================

#============== START INSTANCES =======================================================
#++++++++++++++ APP VAULT MYSQL +++++++++++++++
# Define instance names in an array
variable "instance_names" {
  type    = list(string)
  default = ["vm-app", "vm-vault", "vm-mysql"]  # Update names as needed
}

# Create "vm-app", "vm-vault", "vm-mysql"
resource "google_compute_instance" "vm_instance" {
  count        = length(var.instance_names)  # Create instances based on the number of names in the array
  name         = var.instance_names[count.index]
  machine_type = "e2-small"
  zone         = "asia-southeast1-a"

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2004-focal-v20241219"
    }
  }

  network_interface {
    network = google_compute_network.vpc_network.name  # Reference the VPC network
    subnetwork = google_compute_subnetwork.vpc_subnetwork.name  # Reference the subnetwork

    access_config {
      nat_ip = google_compute_address.static_ip[count.index].address  # Use the reserved static IP
    }
  }

  metadata = {
    "ssh-keys" = "tony:${file("/Users/tony/Documents/study/gcp/key/gcp_key.pub")}"  # Replace with your username and path to your public key
  }

  provisioner "file" {
    # Upload your shell script to the instance
    source      = "../server-install.sh"
    destination = "/tmp/server-install.sh"
  }

  provisioner "remote-exec" {
    inline = [
      "chmod +x /tmp/server-install.sh",  # Make the script executable
      "sudo bash /tmp/server-install.sh" # Run the script
    ]
  }

  connection {
    type        = "ssh"
    user        = "tony"                     # SSH username
    private_key = file("/Users/tony/Documents/study/gcp/key/gcp_key")          # Private key for SSH
    host        = self.network_interface[0].access_config[0].nat_ip # VM's external IP
  }

  metadata_startup_script = "echo 'Hello, World Hi' > index.html ; nohup busybox httpd -f -p 8080 &"

  tags = ["http-server"]  # Adding tags for firewall rules, if necessary
}

#++++++++++++++ Jenkin +++++++++++++++
# Create jenkin server
resource "google_compute_instance" "vm_jenkin" {
  name         = "vm-jenkin"
  machine_type = "e2-small"
  zone         = "asia-southeast1-a"

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2004-focal-v20241219"
    }
  }

  network_interface {
    network = google_compute_network.vpc_network.name  # Reference the VPC network
    subnetwork = google_compute_subnetwork.vpc_subnetwork.name  # Reference the subnetwork

    access_config {
      nat_ip = google_compute_address.jenkin_static_ip.address  # Use the reserved static IP
    }
  }

  metadata = {
    "ssh-keys" = "tony:${file("/Users/tony/Documents/study/gcp/key/gcp_key.pub")}"  # Replace with your username and path to your public key
  }

  provisioner "file" {
    # Upload your shell script to the instance
    source      = "../jenkin-install.sh"
    destination = "/tmp/jenkin-install.sh"
  }

  provisioner "remote-exec" {
    inline = [
      "chmod +x /tmp/jenkin-install.sh",  # Make the script executable
      "sudo bash /tmp/jenkin-install.sh" # Run the script
    ]
  }

  connection {
    type        = "ssh"
    user        = "tony"                     # SSH username
    private_key = file("/Users/tony/Documents/study/gcp/key/gcp_key")          # Private key for SSH
    host        = self.network_interface[0].access_config[0].nat_ip # VM's external IP
  }

//  metadata_startup_script = file("../jenkin-install.sh")

  tags = ["http-server"]  # Adding tags for firewall rules, if necessary
}

#============== END INSTANCES =======================================================



