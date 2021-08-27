import { extendTheme } from '@chakra-ui/react';
import { mode } from '@chakra-ui/theme-tools';

const config: any = {
  initialColorMode: 'dark',
};

const styles = {
  global: (props: any) => ({
    body: {
      bg: mode('gray.100', '#1b1c1d')(props),
    },
  }),
};

const colors = {
  appbar: '#262626',
  accent: '#8e9297',
  active: '#393c43',
  light: '#36393f',
};

const fonts = {
  body: "'Open Sans', sans-serif",
};

const customTheme = extendTheme({ colors, config, styles, fonts });

export default customTheme;

export const scrollbarCss = {
  '&::-webkit-scrollbar': {
    width: '0',
  },
};
