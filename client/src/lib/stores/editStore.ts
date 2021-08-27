import create from 'zustand';

type EditStoreState = {
  title: string;
  setTitle: (title: string) => void;
  body: string;
  setBody: (body: string) => void;
  isDirty: boolean;
  setDirty: (isDirty: boolean) => void;
  reset: () => void;
};

export const editStore = create<EditStoreState>((set) => ({
  title: '',
  setTitle: (title) => set({ title }),
  body: '',
  setBody: (body) => set({ body }),
  isDirty: false,
  setDirty: (isDirty) => set({ isDirty }),
  reset: () =>
    set({
      title: '',
      body: '',
      isDirty: false,
    }),
}));
