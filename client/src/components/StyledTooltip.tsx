import React from 'react';
import { Tooltip } from '@chakra-ui/react';

interface StyledTooltipProps {
  label: string;
}

export const StyledTooltip: React.FC<StyledTooltipProps> = ({
  label,
  children,
}) => (
  <Tooltip
    hasArrow
    label={label}
    placement={'bottom'}
    bg={'#18191c'}
    color={'white'}
    fontWeight={'semibold'}
    py={1}
    px={3}
  >
    {children}
  </Tooltip>
);
