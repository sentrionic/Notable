import Axios, { AxiosResponse } from 'axios';
import { baseUrl } from './constants';
import { AuthInput, Note, NoteInput, TokenResponse } from './models';
import { tokenStore } from './stores/tokenStore';

const { getState } = tokenStore;

const instance = Axios.create({
  baseURL: baseUrl,
  headers: {
    Authorization: `Bearer ${getState().token}`,
  },
});

export const fetchNotes = (): Promise<AxiosResponse<Note[]>> =>
  instance.get('/notes');

export const createNote = (input: NoteInput): Promise<AxiosResponse<Note>> =>
  instance.post('/notes', input);

export const updateNote = (
  id: string,
  input: NoteInput
): Promise<AxiosResponse<Note>> => instance.put(`/notes/${id}`, input);

export const deleteNote = (id: string): Promise<AxiosResponse<Note>> =>
  instance.delete(`/notes/${id}`);

export const fetchDeleted = (): Promise<AxiosResponse<string[]>> =>
  instance.get('/notes/deleted');

export const login = (
  input: AuthInput
): Promise<AxiosResponse<TokenResponse>> =>
  instance.post('/accounts/login', input);

export const register = (
  input: AuthInput
): Promise<AxiosResponse<TokenResponse>> => instance.post('/accounts', input);
