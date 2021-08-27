import {
  Box,
  Button,
  Flex,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  Text,
  useDisclosure,
} from '@chakra-ui/react';
import * as React from 'react';
import { editStore } from '../lib/stores/editStore';
import { Note } from '../lib/models';
import { dataStore } from '../lib/stores/dataStore';

interface NoteListItemProps {
  note: Note;
  index: number;
}

export const NoteListItem: React.FC<NoteListItemProps> = ({ note, index }) => {
  const [current, setCurrent] = dataStore((state) => [
    state.current,
    state.setCurrent,
  ]);

  const [isDirty, reset] = editStore((state) => [state.isDirty, state.reset]);
  const { isOpen, onOpen, onClose } = useDisclosure();
  const isActive = current === index;

  const handleOpen = () => {
    if (isDirty) {
      onOpen();
    } else {
      setCurrent(index);
    }
  };

  const handleClick = () => {
    onClose();
    reset();
    setCurrent(index);
  };

  return (
    <>
      <Flex
        pr="3"
        my={2}
        color={isActive ? '#fff' : 'accent'}
        _hover={{
          bg: 'light',
          borderRadius: '5px',
          cursor: 'pointer',
          color: '#fff',
        }}
        fontStyle={isDirty && isActive ? 'italic' : 'normal'}
        align="center"
        bg={isActive ? 'active' : undefined}
        onClick={handleOpen}
      >
        <Box mr="4" w="4px" h="50px" bg={isActive ? 'accent' : undefined} />
        <Text>{note.title}</Text>
      </Flex>
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Discard Changes</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Text>Are you sure you want to discard your changes?</Text>
          </ModalBody>

          <ModalFooter>
            <Button mr={3} onClick={onClose}>
              Close
            </Button>
            <Button variant="outline" colorScheme="red" onClick={handleClick}>
              Discard
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </>
  );
};
