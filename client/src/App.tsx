import { ChakraProvider } from '@chakra-ui/react';
import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { HashRouter, Route, Switch } from 'react-router-dom';
import customTheme from './lib/customTheme';
import { Auth } from './screens/Auth';
import { Home } from './screens/Home';
import { Splash } from './screens/Splash';
import './styles.css';

const App = () => {
  return (
    <HashRouter>
      <Switch>
        <Route path="/auth" exact component={Auth} />
        <Route path="/home" exact component={Home} />
        <Route path="/" exact component={Splash} />
      </Switch>
    </HashRouter>
  );
};

function render() {
  ReactDOM.render(
    <ChakraProvider theme={customTheme}>
      <App />
    </ChakraProvider>,
    document.getElementById('root')
  );
}

render();
