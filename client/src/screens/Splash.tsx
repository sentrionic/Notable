import { Box, CircularProgress, Flex, Heading, Text } from '@chakra-ui/react';
import React, { useEffect, useState } from 'react';
import { useHistory } from 'react-router-dom';
import { fetchNotes } from '../lib/handler';
import { dataStore } from '../lib/stores/dataStore';
import { tokenStore } from '../lib/stores/tokenStore';

export function Splash() {
  const router = useHistory();
  const { getState } = tokenStore;
  const setNotes = dataStore((state) => state.setNotes);
  const [authChecked, setAuthChecked] = useState(false);

  useEffect(() => {
    if (getState().token) {
      setAuthChecked(true);

      const fetch = async () => {
        const { data } = await fetchNotes();
        setNotes(data);
        router.replace('/home');
      };

      fetch();
    } else {
      router.replace('/auth');
    }
  }, []);

  return (
    <Flex width="full" align="center" justify="center" height="100vh">
      <Box textAlign="center">
        <Heading>Notable</Heading>
        <Text mt="2">
          {authChecked ? 'Syncing Notes' : 'Checking authentication status'}
        </Text>
        {authChecked && (
          <CircularProgress mt="2" isIndeterminate color="grey" />
        )}
      </Box>
    </Flex>
  );
}
