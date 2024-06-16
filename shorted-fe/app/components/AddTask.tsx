"use client";

import { AiOutlinePlus } from "react-icons/ai";
import Modal from "./Modal";
import { FormEventHandler, useState } from "react";
import { addTodo, checkShortedLink } from "../service/api";
import { useRouter } from "next/navigation";
import { v4 as uuidv4 } from "uuid";

const AddTask = () => {
  const router = useRouter();
  const [modalOpen, setModalOpen] = useState<boolean>(false);
  const [newTaskValue, setNewTaskValue] = useState<string>("");
  const [newShortedUrl, setNewShortedUrl] = useState<string>("");

  const handleSubmitNewTodo: FormEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();

    if (newShortedUrl != "" ) {
      const isDupplicate = await checkShortedLink(newShortedUrl);
      console.log("checkShorted " + isDupplicate);
      if (isDupplicate) {
        alert("url is dupplated");
      } else {
        await addTodo({
          id: 0,
          linkOriginal: newTaskValue,
          shortedDomain: "",
          shortedUrl: newShortedUrl
        });
        setNewTaskValue("");
        setNewShortedUrl("");
        setModalOpen(false);
        router.refresh();
      }
    } else {
      await addTodo({
        id: 0,
        linkOriginal: newTaskValue,
        shortedDomain: "",
        shortedUrl: ""
      });
      setNewTaskValue("");
      setNewShortedUrl("");
      setModalOpen(false);
      router.refresh();
    }
  };

  return (
    <div>
      <button
        onClick={() => setModalOpen(true)}
        className='btn btn-primary w-full'
      >
        Add new link <AiOutlinePlus className='ml-2' size={18} />
      </button>

      <Modal modalOpen={modalOpen} setModalOpen={setModalOpen}>
        <form onSubmit={handleSubmitNewTodo}>
          <h3 className='font-bold text-lg'>Add new link</h3>
          <div className='modal-action'>
            <input
              value={newTaskValue}
              onChange={(e) => setNewTaskValue(e.target.value)}
              type='text'
              placeholder='Link original '
              className='input input-bordered w-full'
            />
          </div>
          <div className='modal-action'>
            <input
                value={newShortedUrl}
                onChange={(e) => setNewShortedUrl(e.target.value)}
                type='text'
                placeholder='Custom shorted url'
                className='input input-bordered w-full'
              />
          </div>
          <div className='pt-6 content-center'>
            <button type='submit' className='btn'>
                Submit
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default AddTask;
