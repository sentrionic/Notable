import { Search2Icon } from '@chakra-ui/icons';
import {
  Flex,
  GridItem,
  Input,
  InputGroup,
  InputLeftElement,
} from '@chakra-ui/react';
import React from 'react';
import { dataStore } from '../lib/stores/dataStore';
import { StyledTooltip } from './StyledTooltip';

export const SearchNotesInput: React.FC = () => {
  const [query, setQuery, setCurrent] = dataStore((state) => [
    state.query,
    state.setQuery,
    state.setCurrent,
  ]);

  const handleChange = (value: string) => {
    setCurrent(0);
    setQuery(value);
  };

  return (
    <GridItem rowSpan={1} colSpan={1} bg="appbar">
      <StyledTooltip label="Search Notes">
        <Flex mx={2} h="100%" align="center">
          <InputGroup>
            <InputLeftElement
              pointerEvents="none"
              children={<Search2Icon color="gray.300" />}
            />
            <Input
              w={'280px'}
              placeholder="Search..."
              value={query}
              onChange={(e) => handleChange(e.target.value)}
            />
          </InputGroup>
        </Flex>
      </StyledTooltip>
    </GridItem>
  );
};
