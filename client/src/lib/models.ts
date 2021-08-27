export interface Note {
  id: string;
  title: string;
  body: string;
  updatedAt: string;
  createdAt: string;
}

export interface NoteInput {
  title: string;
  body: string;
}

export interface TokenResponse {
  token: string;
}

export interface AuthInput {
  email: string;
  password: string;
}
