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
				const panel = vscode.window.createWebviewPanel(
					'tips', // Identifies the type of the webview. Used internally
					'Предлагаемые варианты', // Title of the panel displayed to the user
					vscode.ViewColumn.Two, // Editor column to show the new webview panel in.
					{
						// Enable scripts in the webview
						enableScripts: true
					  } // Webview options. More on these later.
				  );
					const mes = `
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title>Подсказки</title>
		<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
		<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
		<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
		<style>
			p, button, div {
				color: #DDD;
			}
			hr {
				border-top: 1px solid rgba(255,255,255,.1);
			}
		</style>
		<script>
			const text_ = 'SOME CODE';
			const vscode = acquireVsCodeApi();
			function useAdvise(){
				console.log(vscode);
				vscode.postMessage({command: 'use',text: text_})
			}
			function hideAdvise(){
				vscode.postMessage({command: 'hide'})
			}
		</script>
	</head>
	<body class="bg-dark">
		<div class="container-fluid">
			<hr />
			<div class="code-wrapper">
			<a href="http://somegreatsite.com">Link Name</a> is a link to another nifty site.
			<h1>This is a Header</h1>
			<h2>This is a Medium Header</h2>
			<p>Send me mail at <a href="mailto:support@yourcompany.com">support@yourcompany.com</a></p>.
			<p>This is a new paragraph!</p> <b>This is a new paragraph!</b><br /> <b><i>This is a new sentence without a paragraph break, in bold italics.</i></b>
			</div>
			<hr />
			<button class="btn btn-outline-light btn-block" onclick="useAdvise()">Ok</button>
			<button class="btn btn-dark btn-block" onclick="hideAdvise()">Скрыть</button>
		</div>
	</body>
</html>
					`;
					panel.webview.html = mes; // htmlString;
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
							});
						  return;
						case 'hide':
							console.log('hide');
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
