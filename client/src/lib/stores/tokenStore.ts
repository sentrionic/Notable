import { persist } from 'zustand/middleware';
import create from 'zustand/vanilla';

type TokenStoreState = {
  token?: string;
};

export const tokenStore = create<TokenStoreState>(
  persist(
    () => ({
      token: null,
    }),
    {
      name: 'token',
    }
  )
);
