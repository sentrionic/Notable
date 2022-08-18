import { ChakraProvider } from '@chakra-ui/react';
import { createRoot } from 'react-dom/client';
import { HashRouter, Route, Routes } from 'react-router-dom';
import customTheme from './lib/customTheme';
import { Auth } from './screens/Auth';
import { Home } from './screens/Home';
import { Splash } from './screens/Splash';
import './styles.css';
import React from 'react';

const App = () => {
  return (
    <HashRouter>
      <Routes>
        <Route path="/auth" element={<Auth />} />
        <Route path="/home" element={<Home />} />
        <Route path="/" element={<Splash />} />
      </Routes>
    </HashRouter>
  );
};

function render() {
  const container = document.getElementById('root');
  const root = createRoot(container!);
  root.render(
    <ChakraProvider theme={customTheme}>
      <App />
    </ChakraProvider>
  );
}

render();
