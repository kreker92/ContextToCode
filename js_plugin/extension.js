// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
const vscode = require('vscode');
const editor = vscode.window.activeTextEditor;
const rp = require("request-promise");

// this method is called when your extension is activated
// your extension is activated the very first time the command is executed

/**
 * @param {vscode.ExtensionContext} context
 */

function activate(context) {

	// Use the console to output diagnostic information (console.log) and errors (console.error)
	// This line of code will only be executed once when your extension is activated
	console.log('"showText" active');
	

	// The command has been defined in the package.json file
	// Now provide the implementation of the command with  registerCommand
	// The commandId parameter must match the command field in package.json
	const commandId = 'extension.showText'
	let disposable = vscode.commands.registerCommand(commandId, function () {
    /* if (context.subscriptions.length > 1) {
      for (i = context.subscriptions.length-1; i > 1;i++) {
        var sub = context.subscriptions[i];
        sub.dispose();
      }
		} */
    const text = editor.document.getText()


		let options = {
			method: 'POST',
			uri: 'http://78.46.103.68:1958/generate',
			body: {
				text: text
			},
			json: true
		};

		rp(options)
      .then(function (htmlString) {
				const panel = vscode.window.createWebviewPanel(
					'tips', // Identifies the type of the webview. Used internally
					'Подсказка jQuery', // Title of the panel displayed to the user
					vscode.ViewColumn.Two, // Editor column to show the new webview panel in.
					{
						// Enable scripts in the webview
						enableScripts: true
					  } // Webview options. More on these later.
				  );
			    panel.webview.html = htmlString;
					// Handle messages from the webview
				  panel.webview.onDidReceiveMessage(
						message => {
							switch (message.command) {
							case 'use':
								console.log('use');
								editor.edit(edit => {
									let pos = new vscode.Position(editor.selection.start.line,
																								editor.selection.start.character)
									edit.insert(pos, message.text);
									panel.dispose()
								});
                return;
              case 'typing':
                console.log('typing');
                panel.webview.html = htmlString;
							case 'hide':
								panel.dispose()
								console.log('hide');
								return;
              }
						},
						undefined,
						context.subscriptions
          );
          panel.onDidDispose(
            () => {
              console.log('disposed');
            },
            null,
            context.subscriptions
          )
			})
			.catch(function(err) {
				console.log(err);
			});
	});
	context.subscriptions.push(disposable);
}
exports.activate = activate;

// this method is called when your extension is deactivated
function deactivate() {
	console.log('showText disactivated');
}

module.exports = {
	activate,
	deactivate
}
