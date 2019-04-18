var res = {
	tabs: {
	},
	body: `
		<!DOCTYPE html>
		<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>Подсказка jQuery</title>
				<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
				<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
				<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
				<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
				<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.6.0/prism.js"></script>
				<script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.15.0/components/prism-javascript.min.js" integrity="sha256-G5C8j/0gBEHgf/x60UZQY5QrKPWdUo2Pgcu4OgU5q7w=" crossorigin="anonymous"></script>
				<script src="https://cdnjs.cloudflare.com/ajax/libs/js-beautify/1.9.1/beautify.js"></script>
				<script src="https://cdnjs.cloudflare.com/ajax/libs/js-beautify/1.9.1/beautify-css.js"></script>
				<script src="https://cdnjs.cloudflare.com/ajax/libs/js-beautify/1.9.1/beautify-html.js"></script>
				<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.6.0/themes/prism-okaidia.css" />
				<style>
					p, button, div, a {
						color: #DDD;
					}
					a:hover {
						color: #6c757d;
					}
					hr {
						border-top: 1px solid rgba(255,255,255,.1);
					}
					input[type="radio"] {
						display:none;
					}
					.btn-toolbar label {
						cursor: pointer;
					}
					@media (min-width: 767px) {
						.language-js {
							height: 100%;
							margin: -5px !important;
						}
					}
					.paddingTop10 {
						padding-top: 10px;
					}
					.nav-pills {
						max-height: 100%;
						overflow: scroll;
					}
				</style>
				<script>
					var resCode = {};
				</script>
			</head>
			<body class="bg-dark">
				<div class="container-fluid">
						<div class="row">
							<div class="col-3">
								<div class="nav flex-column nav-pills" role="tablist" aria-orientation="vertical">TABS</div>
							</div>
							<div class="col-9">
								<div class="tab-content">
									CONTENTS
								</div>
							</div>
						</div>
				</div>
				<script>
					// let acquireVsCodeApi = function(){}; // to test like usual html page
					const vscode = acquireVsCodeApi();
					function useAdvise(tabId){
						var curResCode = resCode['tab'+tabId]
						let text_ = $(document).find("#tip-"+tabId).find(".code-result").text().replace(curResCode.replace1, '').replace(curResCode.replace2, '');
						vscode.postMessage({command: 'use',text: text_})
					}
					function hideAdvise(){
						vscode.postMessage({command: 'hide'})
					}

					$('.tab-pane').each(function(i, tab) {
						$(tab).find('input, select, textarea, radio').on('keyup change', function(ev){
							rewriteTipRes(tab, generateTip($(tab).attr('data-tip')));
						});
						rewriteTipRes(tab, generateTip($(tab).attr('data-tip')));
					});
					var fields = $('.form-edit').find('input, select, textarea, radio');
					
					function generateTip(tabId) {
						var res = resCode['tab'+tabId].template;
						$("#tip-"+tabId).find('input, select, textarea, radio').each(function(i, f) {
							$f = $(f);
							var partNum = +$f.attr('data-part');
							res = res.replace('PART'+partNum, $.trim($f.val()));
						});

						return js_beautify(res, { indent_size: 4 });
					}

					function rewriteTipRes(tab, tip) {
						var $el = $(tab).find(".code-result");
						$el.html(tip);
						Prism.highlightElement($el[0]);
					}
					$(document).ready(function(){
						$('#tip-1-tab').trigger('click');
					});
				</script>
			</body>
		</html>`
};

// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
const vscode = require('vscode');
const workspace = vscode.workspace;
const window = vscode.window;
const rp = require("request-promise");

// this method is called when your extension is activated
// your extension is activated the very first time the command is executed

/**
 * @param {vscode.ExtensionContext} context
 */

function activate(context) {
	// Use the console to output diagnostic information (console.log) and errors (console.error)
	// This line of code will only be executed once when your extension is activated
	console.log('"showText" activated');
	
	
	// The command has been defined in the package.json file
	// Now provide the implementation of the command with  registerCommand
	// The commandId parameter must match the command field in package.json
	const commandId = 'extension.showText'
	let disposable = vscode.commands.registerCommand(commandId, function () {
		const editor = window.activeTextEditor;
		const text = editor.document.getText();

		console.log('For editor "'+ editor._id +'"');

		let options = {
			method: 'POST',
			uri: 'http://78.46.103.68:1958/generate',
			body: {
				text: text
			},
			json: true
		};

		function showRes(panel, res, prefix) {
			console.log("'" + prefix + "'", prefix.length);
			let matchingTabs = Object.keys(res.tabs).filter(function (tab) {
				// console.log(tab, prefix, ~tab.indexOf(prefix));
				return ~tab.indexOf(prefix);
			});
			console.log(matchingTabs);
			
			let tabs = matchingTabs.map(function(key){
				return res.tabs[key].tab;
			}).join('');
			let contents = matchingTabs.map(function (key) {
				return res.tabs[key].content;
			}).join('');
			let html = res.body.replace('TABS', tabs).replace('CONTENTS', contents);
			
			panel.webview.html = html;
		}

		rp(options)
			.then(function (qwe) { // res
				let filter_by_prefix = '';
				const panel = window.createWebviewPanel(
					'tips', // Identifies the type of the webview. Used internally
					'Подсказка jQuery', // Title of the panel displayed to the user
					vscode.ViewColumn.Two, // Editor column to show the new webview panel in.
					{
						// Enable scripts in the webview
						enableScripts: true
					} // Webview options. More on these later.
				);
				showRes(panel, res, filter_by_prefix);
				
				
				// Handle messages from the webview
				// закрывать когда выбираешь другое окошко
				window.onDidChangeActiveTextEditor(
					ev => {
						// console.log(ev._id, editor._id, editor);
						ev && ev._id && ev._id != editor._id && panel.dispose();
					}
				)
				// закрывать когда закрываешь окошко
				workspace.onDidCloseTextDocument(
					textDocument => {
						console.log("closed => " + textDocument.isClosed)
						panel.dispose();
					},
					null,
					context.subscriptions
				);
				// любая клавиша кроме enter - фильтр по префиксу
				// если enter - поиск подсказок
				workspace.onDidChangeTextDocument(
					ev => {
						console.log(ev);
						
						if (ev && ev.contentChanges && ev.contentChanges.length
									 && (ev.contentChanges[0].text || ev.contentChanges[0].rangeLength)) {
							let change = ev.contentChanges[0].text;
							if (~ev.contentChanges[0].text.indexOf("\n")) {
								filter_by_prefix = '';
								options.body.text = editor.document.getText();
								rp(options).then(function(newRes){
									res.tabs = newRes;
									showRes(panel, res, filter_by_prefix);
								});
							} else {
								console.log('backspace');
								// console.log(ev.contentChanges);
								if (ev.contentChanges[0].rangeLength) {
									filter_by_prefix = filter_by_prefix.slice(0, ev.contentChanges[0].rangeOffset)
										+ filter_by_prefix.slice(ev.contentChanges[0].rangeOffset + ev.contentChanges[0].rangeLength);
								} else {
									filter_by_prefix += change;
								}
								console.log('prefix: '+filter_by_prefix);
								showRes(panel, res, filter_by_prefix);
							}
						} else {
							console.error('No changes detected. But it must be.', ev);
						}
					},
					null,
					context.subscriptions
				);

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
