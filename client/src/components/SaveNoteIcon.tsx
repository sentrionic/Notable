import { LockIcon } from '@chakra-ui/icons';
import { IconButton, useToast } from '@chakra-ui/react';
import * as React from 'react';
import { editStore } from '../lib/stores/editStore';
import { updateNote } from '../lib/handler';
import { dataStore } from '../lib/stores/dataStore';
import { StyledTooltip } from './StyledTooltip';

export const SaveNoteIcon: React.FC = () => {
  const [getCurrent, notes, setNotes] = dataStore((state) => [
    state.getCurrent,
    state.notes,
    state.setNotes,
  ]);

  const [isDirty, title, body, reset] = editStore((state) => [
    state.isDirty,
    state.title,
    state.body,
    state.reset,
  ]);

  const note = getCurrent();
  const toast = useToast();

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

  return (
    <StyledTooltip label="Save Changes">
      <IconButton
        aria-label="Save note"
        icon={<LockIcon />}
        onClick={handleSave}
        disabled={!isDirty}
      />
    </StyledTooltip>
  );
};
