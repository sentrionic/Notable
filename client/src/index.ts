import { app, BrowserWindow } from 'electron';

declare const MAIN_WINDOW_WEBPACK_ENTRY: string;

if (require('electron-squirrel-startup')) {
  app.quit();
}

const createWindow = (): void => {
  const mainWindow = new BrowserWindow({
    height: 800,
    width: 1200,
    autoHideMenuBar: true,
    resizable: false,
    fullscreenable: false,
    icon: __dirname + '/assets/icon.ico',
    backgroundColor: '#1b1c1d',
    show: false,
  });

  mainWindow.loadURL(MAIN_WINDOW_WEBPACK_ENTRY);
  //mainWindow.webContents.openDevTools();

  mainWindow.once('ready-to-show', () => {
    mainWindow.show();
  });
};

app.on('ready', createWindow);

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', () => {
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow();
  }
});
