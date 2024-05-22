module.exports = {
    apps: [
        {
            name: "shoted-fe",
            script: "node_modules/next/dist/bin/next",
            args: "start -p 3333", //running on port 3000
            watch: false,
        },
    ],
};
