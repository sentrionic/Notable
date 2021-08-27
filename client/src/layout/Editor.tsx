import { Flex, GridItem, Heading, useToast } from '@chakra-ui/react';
import MDEditor from '@uiw/react-md-editor';
import * as React from 'react';
import { useEffect } from 'react';
import Hotkeys from 'react-hot-keys';
import { gridHeight } from '../lib/constants';
import { updateNote } from '../lib/handler';
import { dataStore } from '../lib/stores/dataStore';
import { editStore } from '../lib/stores/editStore';

export const Editor: React.FC<{}> = ({}) => {
  const [notes, setNotes, getCurrent, mode, setMode, preview, setPreview] =
    dataStore((state) => [
      state.notes,
      state.setNotes,
      state.getCurrent,
      state.mode,
      state.setMode,
      state.preview,
      state.setPreview,
    ]);

  const [title, body, setBody, reset, setDirty] = editStore((state) => [
    state.title,
    state.body,
    state.setBody,
    state.reset,
    state.setDirty,
  ]);

  const note = getCurrent();
  const toast = useToast();

  if (!note) {
    return (
      <GridItem w="100%" h="100%" colSpan={1 / 2}>
        <Flex bg="appbar" h="100%" align="center" justify="center">
          <Heading>Create a note to start</Heading>
        </Flex>
      </GridItem>
    );
  }

  useEffect(() => {
    setBody(note.body);
  }, [note]);

  const handleChange = (value: string) => {
    setBody(value);
    setDirty(true);
  };

  const handleSave = async () => {
    const { data } = await updateNote(note.id, {
      title: title,
      body: body,
    });

    if (data) {
      const newNotes = notes.map((n) => {
        if (n.id === data.id) return data;
        else return n;
      });

      reset();
      setNotes(newNotes);

      toast({
        title: 'Note updated',
        status: 'success',
        duration: 3000,
        isClosable: true,
      });
    }
  };

  const onKeyDown = (keyName: any, e: any, handle: any) => {
    if (keyName === 'ctrl+s') {
      handleSave();
    } else if (keyName === 'ctrl+t') {
      if (preview === 'edit') {
        setPreview('preview');
      } else {
        setPreview('edit');
      }
    } else if (keyName === 'ctrl+q') {
      if (mode === 'create') {
        setMode('search');
      } else {
        setMode('create');
      }
    }
  };

  return (
    <GridItem w="100%" h="100%" colSpan={1 / 2}>
      <Hotkeys keyName="ctrl+s, ctrl+t, ctrl+q" onKeyDown={onKeyDown}>
        <MDEditor
          height={gridHeight}
          value={body}
          onChange={(e) => handleChange(e)}
          preview={preview}
          visiableDragbar={false}
        />
      </Hotkeys>
    </GridItem>
  );
};
