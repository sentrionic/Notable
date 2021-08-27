import { AddIcon } from '@chakra-ui/icons';
import {
  GridItem,
  Flex,
  InputGroup,
  InputLeftElement,
  Input,
} from '@chakra-ui/react';
import React, { FormEvent, useState } from 'react';
import { dataStore } from '../lib/stores/dataStore';
import { createNote } from '../lib/handler';
import { StyledTooltip } from './StyledTooltip';

export const CreateNoteInput: React.FC = () => {
  const [title, setTitle] = useState('');
  const [notes, setNotes, setCurrent] = dataStore((state) => [
    state.notes,
    state.setNotes,
    state.setCurrent,
  ]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (title.trim().length === 0) {
      return;
    }

    const { data } = await createNote({ title: title.trim(), body: '' });

    setNotes([data, ...notes]);
    setCurrent(0);

    setTitle('');
  };

  return (
    <GridItem rowSpan={1} colSpan={1} bg="appbar">
      <StyledTooltip label="Press enter to create a note">
        <Flex mx={2} h="100%" align="center">
          <form onSubmit={handleSubmit}>
            <InputGroup>
              <InputLeftElement
                pointerEvents="none"
                children={<AddIcon color="gray.300" />}
              />
              <Input
                w={'280px'}
                placeholder="New note"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
              />
            </InputGroup>
          </form>
        </Flex>
      </StyledTooltip>
    </GridItem>
  );
};
