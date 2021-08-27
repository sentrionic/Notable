import { Grid } from '@chakra-ui/react';
import React from 'react';
import { AppBar } from '../layout/AppBar';
import { Editor } from '../layout/Editor';
import { NotesList } from '../layout/NotesList';

export const Home = () => {
  return (
    <Grid
      overflowY="hidden"
      h="100%"
      templateRows="65px 1fr;"
      templateColumns="minmax(300px, 25%) 1fr;"
      gap={1}
      bg="grey"
    >
      <AppBar />
      <NotesList />
      <Editor />
    </Grid>
  );
};
