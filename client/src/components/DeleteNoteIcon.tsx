import { DeleteIcon } from '@chakra-ui/icons';
import {
  Button,
  IconButton,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  useDisclosure,
  Text,
  useToast,
} from '@chakra-ui/react';
import * as React from 'react';
import { deleteNote } from '../lib/handler';
import { dataStore } from '../lib/stores/dataStore';
import { StyledTooltip } from './StyledTooltip';

export const DeleteNoteIcon: React.FC = () => {
  const [getCurrent, notes, setNotes, current, setCurrent] = dataStore(
    (state) => [
      state.getCurrent,
      state.notes,
      state.setNotes,
      state.current,
      state.setCurrent,
    ]
  );
  const { isOpen, onOpen, onClose } = useDisclosure();
  const toast = useToast();

  const handleDelete = async () => {
    const { data } = await deleteNote(getCurrent().id);

    if (data) {
      onClose();
      const newData = notes.filter((n) => n.id !== data.id);
      setNotes(newData);
      const newIndex = Math.max(current - 1, 0);
      setCurrent(newIndex);

      toast({
        title: 'Note deleted',
        description: 'Successfully deleted the note',
        status: 'success',
        duration: 3000,
        isClosable: true,
      });
    }
  };

  return (
    <>
      <StyledTooltip label="Delete note">
        <IconButton
          aria-label="Delete note"
          icon={<DeleteIcon color="#f04747" />}
          onClick={onOpen}
        />
      </StyledTooltip>
      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Delete Note</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Text>
              Are you sure you want to delete this note? This cannot be undone
            </Text>
          </ModalBody>

          <ModalFooter>
            <Button mr={3} onClick={onClose}>
              Cancel
            </Button>
            <Button colorScheme="red" onClick={handleDelete}>
              Delete
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </>
  );
};
