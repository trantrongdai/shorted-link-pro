import { ITask } from "../../types/tasks";

const baseUrl = 'http://34.142.136.210:8080';

//const baseUrl = 'http://localhost:8080';

export const getAllTodos = async (): Promise<ITask[]> => {
  const res = await fetch(`${baseUrl}/shortedLinks`, { cache: 'no-store' });
  const todos = await res.json();
  return todos;
}

export const checkShortedLink = async (url: string): Promise<ITask[]> => {
  const res = await fetch(`${baseUrl}/shortedLinks/check/${url}`, { cache: 'no-store' });
  const todos = await res.json();
  return todos;
}

export const addTodo = async (todo: ITask): Promise<ITask> => {
  const res = await fetch(`${baseUrl}/shortedLinks`, {
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
  const res = await fetch(`${baseUrl}/shortedLinks/${todo.id}`, {
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
  await fetch(`${baseUrl}/shortedLinks/${id}`, {
    method: 'DELETE',
  })
}
