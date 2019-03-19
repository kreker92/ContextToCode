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
		const text = editor.document.getText()

		let options = {
			method: 'GET',
			uri: 'http://78.46.103.68:8081/',
			body: {
				text: text
			},
			json: true
		}
		rp(options)
			.then(function (htmlString) {
				var panel = vscode.window.createWebviewPanel(
					'tips', // Identifies the type of the webview. Used internally
					'Предлагаемые варианты', // Title of the panel displayed to the user
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
						case 'info':
							editor.edit(edit => {
								edit.insert(new vscode.Position(0, 0), message.text);
							});
						  return;
					  }
					},
					undefined,
					context.subscriptions
				  );
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
	console.log('showText disactive');
}

module.exports = {
	activate,
	deactivate
}
