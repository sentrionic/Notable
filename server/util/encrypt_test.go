package util

import (
	"crypto/rand"
	"encoding/hex"
	"github.com/stretchr/testify/require"
	"testing"
)

func TestEncryption(t *testing.T) {
	bytes := make([]byte, 32)
	_, err := rand.Read(bytes)
	require.NoError(t, err)

	key := hex.EncodeToString(bytes)
	text := RandomString(8)

	encrypted, err := Encrypt(text, key)
	require.NoError(t, err)
	require.NotEmpty(t, encrypted)

	decrypted, err := Decrypt(encrypted, key)
	require.NoError(t, err)
	require.Equal(t, text, decrypted)

	bytes2 := make([]byte, 32)
	_, err = rand.Read(bytes2)
	require.NoError(t, err)

	wrongKey := hex.EncodeToString(bytes2)
	decrypted2, err := Decrypt(encrypted, wrongKey)
	require.Error(t, err)
	require.NotEqual(t, text, decrypted2)

	encrypted2, err := Encrypt(text, wrongKey)
	require.NoError(t, err)
	require.NotEmpty(t, encrypted2)
	require.NotEqual(t, encrypted, encrypted2)
}
