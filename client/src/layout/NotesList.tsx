import { GridItem } from '@chakra-ui/react';
import * as React from 'react';
import { NoteListItem } from '../components/NoteListItem';
import { gridHeight } from '../lib/constants';
import { scrollbarCss } from '../lib/customTheme';
import { dataStore } from '../lib/stores/dataStore';

export const NotesList: React.FC<{}> = ({}) => {
  // Also fetch notes to listen to updates
  const [searchNotes, _] = dataStore((state) => [
    state.searchNotes,
    state.notes,
  ]);

  return (
    <GridItem
      colSpan={1}
      w="100%"
      h={gridHeight}
      bg="#161616"
      overflowY="hidden"
      _hover={{ overflowY: 'auto' }}
      css={scrollbarCss}
    >
      {searchNotes().map((n, i) => (
        <NoteListItem note={n} index={i} key={n.id} />
      ))}
    </GridItem>
  );
};
