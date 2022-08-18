import { persist } from 'zustand/middleware';
import create from 'zustand/vanilla';

type TokenStoreState = {
  token?: string;
};

export const tokenStore = create(
  persist<TokenStoreState>(
    () => ({
      token: null,
    }),
    {
      name: 'token',
    }
  )
);
