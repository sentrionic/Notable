import { Box, Flex, GridItem, HStack, Input } from '@chakra-ui/react';
import * as React from 'react';
import { useEffect } from 'react';
import { editStore } from '../lib/stores/editStore';
import { CreateNoteInput } from '../components/CreateNoteInput';
import { DeleteNoteIcon } from '../components/DeleteNoteIcon';
import { SaveNoteIcon } from '../components/SaveNoteIcon';
import { ToggleDisplayIcon } from '../components/ToggleDisplayIcon';
import { dataStore } from '../lib/stores/dataStore';
import { SearchNotesInput } from '../components/SearchNotesInput';
import { LogoutIcon } from '../components/LogoutIcon';
import { StyledTooltip } from '../components/StyledTooltip';

export const AppBar: React.FC<{}> = ({}) => {
  const [getCurrent, mode] = dataStore((state) => [
    state.getCurrent,
    state.mode,
  ]);

  const [title, setTitle, isDirty, setDirty] = editStore((state) => [
    state.title,
    state.setTitle,
    state.isDirty,
    state.setDirty,
  ]);

  const note = getCurrent();

  if (!note) {
    return (
      <React.Fragment>
        <CreateNoteInput />
        <GridItem rowSpan={1} colSpan={1 / 2} bg="appbar">
          <Flex mx={4} h="100%" w="100%" align="center" justify="flex-end">
            <LogoutIcon />
          </Flex>
        </GridItem>
      </React.Fragment>
    );
  }

  useEffect(() => {
    setTitle(note.title);
  }, [note]);

  const handleChange = (value: string) => {
    setTitle(value);
    setDirty(true);
  };

  return (
    <React.Fragment>
      {mode === 'create' ? <CreateNoteInput /> : <SearchNotesInput />}
      <GridItem rowSpan={1} colSpan={1 / 2} bg="appbar">
        <Flex mx={4} h="100%" align="center" justify="space-between">
          <HStack spacing="4">
            <StyledTooltip label="Note Title">
              <Input
                w={'280px'}
                variant="flushed"
                placeholder="Title"
                value={title}
                fontSize="18px"
                fontStyle={isDirty ? 'italic' : 'normal'}
                onChange={(e) => handleChange(e.target.value)}
              />
            </StyledTooltip>
            <Box w="20px" />
            <SaveNoteIcon />
            <ToggleDisplayIcon />
            <DeleteNoteIcon />
          </HStack>
          <LogoutIcon />
        </Flex>
      </GridItem>
    </React.Fragment>
  );
};
