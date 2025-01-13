import { ITask } from "../../types/tasks";

  // call direct in local
  //const baseUrlOut = 'http://localhost:8080';
  //const baseUrlInside = 'http://localhost:8080';

// call through docker
//const baseUrlOut = 'http://localhost:8080';
//const baseUrlInside = 'http://192.168.1.8:8080';

// test webhook update

// call in server
const baseUrlOut = 'http://34.87.4.192:8080';
const baseUrlInside = 'http://shorted-be:8080';

// test 6
export const getAllTodos = async (): Promise<ITask[]> => {
  const res = await fetch(`${baseUrlInside}/shortedLinks`, { cache: 'no-store' });
  const todos = await res.json();
  return todos;
}

export const checkShortedLink = async (url: string): Promise<ITask[]> => {
  const res = await fetch(`${baseUrlOut}/shortedLinks/check/${url}`, { cache: 'no-store' });
  const todos = await res.json();
  return todos;
}

export const addTodo = async (todo: ITask): Promise<ITask> => {
  const res = await fetch(`${baseUrlOut}/shortedLinks`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(todo)
  })
  const newTodo = await res.json()

  .catch((error) => {
    alert(error);
  });
  console.log("newTodo" + newTodo)
  return newTodo;
}

export const editTodo = async (todo: ITask): Promise<ITask> => {
  const res = await fetch(`${baseUrlOut}/shortedLinks/${todo.id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(todo)
  })
  const updatedTodo = await res.json();
  return updatedTodo;
}

export const deleteTodo = async (id: number): Promise<void> => {
  await fetch(`${baseUrlOut}/shortedLinks/${id}`, {
    method: 'DELETE',
  })
}
