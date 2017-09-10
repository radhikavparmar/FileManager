# FileManager

Simple File Manager
- Application will enable users a simple folder listing
- Application must contain ActionBar (Toolbar) with refresh and settings action buttons. When user clicks on a settings button, the app will start a new activity, where user can change the default folder (through classic settings row called "Default folder").
- When app starts the default folder will be loaded (in background thread)
- Implement every folder as separate fragment.Files will be listed as list (in portrait) or grid (in landscape)
- When user clicks on a folder - open it with animation
- When user clicks on a file - application tries to open the file through default application for the given file type
- Long press on a item will start CAB. CAB will contain one action button - delete. When user clicks on delete it will delete selected files/folders (with confirmation dialog).
