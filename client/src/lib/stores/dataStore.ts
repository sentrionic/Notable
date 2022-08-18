import { PreviewType } from '@uiw/react-md-editor';
import create from 'zustand';
import { persist } from 'zustand/middleware';
import { Note } from '../models';

type InputMode = 'create' | 'search';

type DataStoreState = {
  current: number;
  setCurrent: (index: number) => void;
  notes: Note[];
  searchNotes: () => Note[];
  setNotes: (data: Note[]) => void;
  preview: PreviewType;
  setPreview: (preview: PreviewType) => void;
  getCurrent: () => Note;
  mode: InputMode;
  setMode: (mode: InputMode) => void;
  query: string;
  setQuery: (query: string) => void;
  reset: () => void;
};

export const dataStore = create(
  persist<DataStoreState>(
    (set, get) => ({
      current: 0,
      setCurrent: (index) => set({ current: index }),
      notes: [],
      searchNotes: () => {
        const query = get().query;
        const mode = get().mode;
        const notes = get().notes;

        if (mode === 'create') {
          return notes;
        } else {
          return notes.filter((n) => {
            return (
              n.title.toLocaleLowerCase().includes(query) ||
              n.body.toLocaleLowerCase().includes(query)
            );
          });
        }
      },
      setNotes: (data) => set({ notes: data }),
      preview: 'preview',
      setPreview: (newPreview) => set({ preview: newPreview }),
      getCurrent: () => get().notes[get().current],
      mode: 'create',
      setMode: (mode) => set({ mode }),
      query: '',
      setQuery: (query) => set({ query }),
      reset: () =>
        set({
          current: 0,
          notes: [],
          preview: 'preview',
          mode: 'create',
          query: '',
        }),
    }),
    {
      name: 'store',
    }
  )
);
