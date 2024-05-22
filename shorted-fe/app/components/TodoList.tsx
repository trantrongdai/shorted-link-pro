import { ITask } from "@/types/tasks";
import React from "react";
import Task from "./Task";

interface TodoListProps {
  tasks: ITask[];
}

const TodoList: React.FC<TodoListProps> = ({ tasks }) => {
  return (
    <div className='overflow-x-auto'>
      <table className='table-fixed'>
        {/* head */}
        <thead>
          <tr>
            <th className="w-1/2 px-4 py-2">Link original</th>
            <th className="w-1/4 px-4 py-2">Shorted link</th>
            <th className="w-1/4 px-4 py-2">Actions</th>
          </tr>
        </thead>
        <tbody>
          {tasks.map((task) => (
            <Task key={task.id} task={task} />
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TodoList;
