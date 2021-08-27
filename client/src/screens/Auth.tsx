import {
  Button,
  Flex,
  Heading,
  Input,
  useToast,
  VStack,
} from '@chakra-ui/react';
import React, { FormEvent, useState } from 'react';
import { useHistory } from 'react-router-dom';
import { login, register } from '../lib/handler';
import { tokenStore } from '../lib/stores/tokenStore';

export function Auth() {
  const router = useHistory();
  const { setState } = tokenStore;
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const toast = useToast();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      const { data } = await login({ email, password });
      setState({ token: data.token });
      router.replace('/');
    } catch (err) {
      if (err?.response?.status === 404) {
        toast({
          title: `Invalid Credentials`,
          status: 'error',
          isClosable: true,
          duration: 3000,
        });
      }
    }
  };

  const handleRegister = async () => {
    try {
      const { data } = await register({ email, password });
      setState({ token: data.token });
      router.replace('/');
    } catch (err) {
      let title = 'Something went wrong, try again later';

      if (err?.response?.status === 400) {
        title = 'Bad Request';
      } else if (err?.response?.status === 403) {
        title = 'Email already in use';
      }

      toast({
        title: title,
        status: 'error',
        isClosable: true,
        duration: 3000,
      });
    }
  };

  return (
    <Flex width="full" align="center" justify="center" height="100vh">
      <form onSubmit={handleSubmit}>
        <VStack spacing="6" w="300px" textAlign="center">
          <Heading>Authentication</Heading>

          <Input
            placeholder="Email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <Input
            placeholder="Password"
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <Button w="100%" onClick={handleSubmit}>
            Login
          </Button>
          <Button w="100%" onClick={handleRegister}>
            Register
          </Button>
        </VStack>
      </form>
    </Flex>
  );
}
