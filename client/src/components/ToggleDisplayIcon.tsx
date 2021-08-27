import { EditIcon } from '@chakra-ui/icons';
import { IconButton } from '@chakra-ui/react';
import * as React from 'react';
import { dataStore } from '../lib/stores/dataStore';
import { StyledTooltip } from './StyledTooltip';

export const ToggleDisplayIcon: React.FC = () => {
  const [preview, setPreview] = dataStore((state) => [
    state.preview,
    state.setPreview,
  ]);

  const toggleDisplay = async () => {
    if (preview === 'edit') {
      setPreview('preview');
    } else {
      setPreview('edit');
    }
  };

  return (
    <StyledTooltip label="Toggle Display">
      <IconButton
        aria-label="Toggle display"
        icon={<EditIcon />}
        onClick={toggleDisplay}
      />
    </StyledTooltip>
  );
};
