import { ArrowForwardIcon } from '@chakra-ui/icons';
import { IconButton, useToast } from '@chakra-ui/react';
import * as React from 'react';
import { useNavigate } from 'react-router-dom';
import { dataStore } from '../lib/stores/dataStore';
import { tokenStore } from '../lib/stores/tokenStore';
import { StyledTooltip } from './StyledTooltip';

export const LogoutIcon: React.FC = () => {
  const reset = dataStore((state) => state.reset);
  const { setState } = tokenStore;
  const toast = useToast();
  const navigate = useNavigate();

  const handleLogout = async () => {
    reset();
    setState({ token: null });
    toast({
      title: 'Logout success',
      status: 'success',
      duration: 3000,
      isClosable: true,
    });
    navigate('/auth', { replace: true });
  };

  return (
    <StyledTooltip label="Logout">
      <IconButton
        aria-label="Logout"
        icon={<ArrowForwardIcon />}
        onClick={handleLogout}
        ml={2}
      />
    </StyledTooltip>
  );
};
