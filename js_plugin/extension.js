var res = {
	main_tabs: ['ajax', 'ajax1'],
	else_tabs: ['alert'],
	tabs: {
		ajax: {
			tab: '<a class="nav-link main_tab" id="tip-1-tab" data-toggle="pill" href="#tip-1" role="tab" aria-controls="tip-1" aria-selected="false">jQuery.ajax</a>',
			content: `
			<div class="tab-pane" id="tip-1" role="tabpanel" aria-labelledby="tip-1-tab" data-tip="1">
				<script>
				resCode['tab1'] = {
					replace1: '$.ajax({',
					replace2: '});',
					template: \`  $.ajax({
                    url: PART1,
                    async: PART2,
                    type: PART3,
                    error: PART4,
                    success: PART5
                  });
                  \`
                };
              </script>
	<div class="code-wrapper">
		<h3>Функция отправки AJAX запроса библиотеки jQuery .ajax()</h3>
		<p>С помощи этой функции вы можете отсылать запросы AJAX из скрипта JavaScript.</p>
		<p>Параметры: <b>url</b> - адрес, на который будте отправлен запрос, <b>async</b> - true, если вы хотите чтобы был отправлен асинхронный запрос (по умолчанию - false),
                <b>type</b> - тип запроса GET или POST (по умолчанию, GET), <b>error</b> - функция, которая будет выполнена в случае ошибки при выполнении отправки запроса <b>success</b> - функция, которая будет выполнена в случае успешного выполнения запроса</p>
	</div>
	<hr />
	<div class="row">
		<div class="col-12 col-md-6">
			<div class="form-edit">
				<p><b>$.ajax({</b></p>
					<div class="input-group mb-3">
						<div class="input-group-prepend">
							<span class="input-group-text">url:</span>
						</div>
						<input data-part="1" type="text" class="form-control" aria-label="URL, на который будет отправлен запрос" />
					</div>

					<div class="input-group mb-3">
						<div class="input-group-prepend">
							<span class="input-group-text">async:</span>
						</div>
						<div class="input-group-append">
							<select data-part="2" class="form-control">
								<option value="false">false</option>
								<option value="true">true</option>
							</select>
						</div>
					</div>

					<div class="input-group mb-3">
						<div class="input-group-prepend">
							<span class="input-group-text">type:</span>
						</div>
						<div class="input-group-append">
							<select data-part="3" class="form-control">
								<option value="GET">GET</option>
								<option value="POST">POST</option>
							</select>
						</div>
					</div>

					<div class="input-group mb-3">
						<div class="input-group-prepend">
							<span class="input-group-text">error:</span>
						</div>
						<textarea data-part="4" class="form-control" rows="4">function (content) {
							$menucontainer.html('')
						}
						</textarea>
					</div>

					<div class="input-group mb-3">
						<div class="input-group-prepend">
							<span class="input-group-text">success:</span>
						</div>
						<textarea data-part="5" class="form-control" rows="4">function (content) {
							$menucontainer.html('')
						}
						</textarea>
					</div>
					<b>});</b>
                  </div>
			</div>
			<div class="col-12 col-md-6">
				<pre><code class="language-js code-result"></code></pre>
			</div>
		</div>
		<div class="row paddingTop10">
			<div class="col-12">
				<!-- <div class="mark bg-dark">
					<div class="btn-toolbar">
						<div class="btn-group btn-group-sm btn-block form-group" data-toggle="buttons">
							<label class="btn btn-outline-light">
								<input type="radio" name="options" value="useful"> Useful
                        </label>
								<label class="btn btn-outline-light">
									<input type="radio" name="options" value="rel+"> Rel+
                        </label>
									<label class="btn btn-outline-light">
										<input type="radio" name="options" value="rel-"> Rel-
                        </label>
										<label class="btn btn-outline-light">
											<input type="radio" name="options" value="notrel"> Not rel
                        </label>
											<label class="btn btn-outline-light">
												<input type="radio" name="options" value="stupid"> Stupid
                        </label>
												<label class="btn btn-outline-light">
													<input type="radio" name="options" value=""> No mark
                        </label>
                      </div>
                    </div>
											<div class="form-group">
												<textarea class="form-control" rows="3" placeholder="Комментарий"></textarea>
											</div>
											<div class="form-group">
												<button type="button" class="btn btn-outline-info btn-block">Отправить</button>
											</div>
                  </div> -->
                  <button class="btn btn-outline-light btn-block" onclick="useAdvise(1)">Вставить код</button>
										<!--<button class="btn btn-dark btn-block" onclick="hideAdvise()">Скрыть</button>-->
                </div>
              </div>
            </div>
						`
		},
		ajax1: {
			tab: '<a class="nav-link main_tab" id="tip-2-tab" data-toggle="pill" href="#tip-2" role="tab" aria-controls="tip-1" aria-selected="false">alert</a>',
			content: `
			<div class="tab-pane" id="tip-2" role="tabpanel" aria-labelledby="tip-2-tab" data-tip="2">
              <script>
                resCode['tab2'] = {
                  replace1: '$.ajax1({',
                  replace2: '});;',
                  template: \`  $.ajax1({
										url: PART1,
										success: PART2
									});;
                  \`
                };
              </script>
              <div class="code-wrapper">
                <h3>Функция AJAX1</h3>
                <p>С помощи этой функции вы можете отсылать запросы AJAX1 из скрипта JavaScript.</p>
              </div>
              <hr />
              <div class="row">
                <div class="col-12 col-md-6">
                  <div class="form-edit">
                    <p><b>$.ajax1({</b></p>
                    <div class="input-group mb-3">
                      <div class="input-group-prepend">
                      <span class="input-group-text">url:</span>
                      </div>
                      <input data-part="1" type="text" class="form-control" aria-label="URL, на который будет отправлен запрос" />
                    </div>
                    
                    <div class="input-group mb-3">
                      <div class="input-group-prepend">
                      <span class="input-group-text">success:</span>
                      </div>
                      <textarea data-part="2" class="form-control" rows="4">function (content) {
                          $menucontainer.html('')
                        }
                      </textarea>
                    </div>
                    <b>});;</b>
                  </div>
                </div>
                <div class="col-12 col-md-6">
                  <pre><code class="language-js code-result"></code></pre>
                </div>
              </div>
              <div class="row paddingTop10">
                <div class="col-12">
                  <button class="btn btn-outline-light btn-block" onclick="useAdvise(2)">Вставить код</button>
                </div>
              </div>
            </div>
			`
		},
		alert: {
			tab: '<a class="nav-link" id="tip-3-tab" data-toggle="pill" href="#tip-3" role="tab" aria-controls="tip-1" aria-selected="false">alert</a>',
			content: `
			<div class="tab-pane" id="tip-3" role="tabpanel" aria-labelledby="tip-3-tab" data-tip="3">
              <script type="text/javascript">
                resCode['tab3'] = {
                  replace1: '$.ajax1({',
                  replace2: '});;',
                  template: \`  $.ajax1({
										url: PART1,
										success: PART2
									});;
                  \`
                };
              </script>
              qwe
              <input data-part="1" type="text" class="form-control" aria-label="URL, на который будет отправлен запрос" />
              <textarea data-part="2" class="form-control" rows="4">function (content) {
                  $menucontainer.html('')
                }
              </textarea>
              <code class="language-js code-result"></code>
            </div>
			`
		}
	},
	body: `
		<!DOCTYPE html>
		<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>TITLE</title>
				EXTERNAL_CSS
				EXTERNAL_JS
				<script>
					"use strict";
					var resCode = {};

					// let acquireVsCodeApi = function(){}; // to test like usual html page
					const vscode = acquireVsCodeApi();

					function useAdvise(tabId) {
						let curResCode = resCode['tab' + tabId]
						let text_ = $(document).find("#tip-" + tabId).find(".code-result").text().replace(curResCode.replace1, '').replace(curResCode.replace2, '');
						vscode.postMessage({ command: 'use', text: text_ })
					}
					function hideAdvise() {
						vscode.postMessage({ command: 'hide' })
					}

					// const fields = $('.form-edit').find('input, select, textarea, radio');

					function generateTip(tabId) {
						// console.log(tabId, !!resCode['tab'+tabId]);
						let res = resCode['tab' + tabId].template;
						$("#tip-" + tabId).find('input, select, textarea, radio').each(function (i, f) {
							let $f = $(f);
							let partNum = +$f.attr('data-part');
							res = res.replace('PART' + partNum, $.trim($f.val()));
						});

						return js_beautify(res, { indent_size: 4 });
					}

					function rewriteTipRes(tab, tip) {
						let $el = $(tab).find(".code-result");
						$el.html(tip);
						// console.log($el[0]);
						Prism.highlightElement($el[0]);
					}
					$(document).ready(function () {
						let activePill = null;
						$('.nav-link').on('click', function () {
							activePill = this;
							$('.selectedNav').removeClass('selectedNav');
							$(activePill).addClass('selectedNav');
						});
						$(".nav-link").length && $($(".nav-link")[0]).trigger('click');

						$('.nav-pills > a').on('mouseenter', function (ev) {
							$(this).addClass('hovered');
							$(this).tab('show');
						});

						$('.nav-pills > a').on('mouseleave', function (ev) {
							$('.hovered').removeClass('hovered');
							activePill && !$('.hovered').length && $(activePill).tab('show');
						});

						$('.tab-pane').each(function (i, tab) {
							$(tab).find('input, select, textarea, radio').on('keyup change', function (ev) {
								rewriteTipRes(tab, generateTip($(tab).attr('data-tip')));
							});
							rewriteTipRes(tab, generateTip($(tab).attr('data-tip')));
						});


						// Minimum resizable area
						var minWidth = 60;
						var minHeight = 40;

						// Thresholds
						var FULLSCREEN_MARGINS = -10;
						var MARGINS = 4;

						// End of what's configurable.
						var clicked = null;
						var onLeftEdge;

						var rightScreenEdge, bottomScreenEdge;

						var preSnapped;

						var b, x, y;

						var redraw = false;

						var pane = document.getElementById('content');

						function setBounds(element, w) {
							element.style.width = w + 'px';
						}


						// Mouse events
						pane.addEventListener('mousedown', onMouseDown);
						document.addEventListener('mousemove', onMove);
						document.addEventListener('mouseup', onUp);

						// Touch events 
						pane.addEventListener('touchstart', onTouchDown);
						document.addEventListener('touchmove', onTouchMove);
						document.addEventListener('touchend', onTouchEnd);


						function onTouchDown(e) {
							onDown(e.touches[0]);
							e.preventDefault();
						}

						function onTouchMove(e) {
							onMove(e.touches[0]);
						}

						function onTouchEnd(e) {
							if (e.touches.length == 0) onUp(e.changedTouches[0]);
						}

						function onMouseDown(e) {
							let res = onDown(e);
							res && e.preventDefault();
						}

						function onDown(e) {
							calc(e);

							var isResizing = onLeftEdge;

							clicked = {
								x: x,
								y: y,
								cx: e.clientX,
								cy: e.clientY,
								w: b.width,
								h: b.height,
								isResizing: isResizing,
								onLeftEdge: onLeftEdge,
							};

							return isResizing;
						}

						function calc(e) {
							b = pane.getBoundingClientRect();
							x = e.clientX - b.left;
							y = e.clientY - b.top;

							onLeftEdge = x < MARGINS;

							rightScreenEdge = window.innerWidth - MARGINS;
						}

						var e;

						function onMove(ee) {
							calc(ee);

							e = ee;

							redraw = true;

						}

						function animate() {

							requestAnimationFrame(animate);

							if (!redraw) return;

							redraw = false;

							if (clicked && clicked.isResizing) {
								if (clicked.onLeftEdge) {
									var currentWidth = Math.max(clicked.cx - e.clientX + clicked.w, minWidth);
									if (currentWidth > minWidth) {
										pane.style.width = currentWidth + 'px';
									}
								}


								return;
							} else if (clicked) {
								return true;
							}

							// This code executes when mouse moves without clicking

							// style cursor
							if (onLeftEdge) {
								pane.style.cursor = 'ew-resize';
							} else {
								pane.style.cursor = 'default';
							}
						}

						animate();

						function onUp(e) {
							calc(e);

							clicked = null;
						}

					});
				</script>
			</head>
			<body>
				<div class="container-fluid">
					<div class="row">
						<div class="btns">
							<div class="nav flex-column nav-pills" role="tablist" aria-orientation="vertical">
								MAINTABS
								ELSETABS
							</div>
						</div>
						<div class="content" id="content">
							<div class="tab-content">
								CONTENTS
							</div>
						</div>
					</div>
				</div>
			</body>
		</html>`
};

// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
const vscode = require('vscode');
const workspace = vscode.workspace;
const window = vscode.window;
const rp = require("request-promise");
const path = require('path');

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

		function get_scripts() {
			const styles = ['bootstrap.min.css', 'prism-okaidia.css', 'style.css', 'inter.css'];
			const js = ['jquery-3.3.1.slim.min.js', 'bootstrap.min.js', 'popper.min.js', 'prism.js', 'prism-javascript.min.js', 'beautify.js', 'beautify-css.js', 'beautify-html.js'];

			let externalCSS = [];
			let externalScripts = [];
			for(let i=0, len=styles.length;i<len;i++) {
				let cssFile = styles[i];
				cssFile = vscode.Uri.file(path.join(context.extensionPath, 'assets', 'css', cssFile)).with({ scheme: 'vscode-resource' });
				externalCSS.push('<link rel="stylesheet" href="' + cssFile +'">');
			}
			for (let i = 0, len = js.length; i < len; i++) {
				let jsFile = js[i];
				jsFile = vscode.Uri.file(path.join(context.extensionPath, 'assets', 'js', jsFile)).with({ scheme: 'vscode-resource' });
				externalScripts.push('<script src="' + jsFile + '"></script>');
			}

			return [externalCSS.join("\n"), externalScripts.join("\n")];
		}

		function showRes(panel, res, prefix) {
			// console.log("'" + prefix + "'", prefix.length);
			let matchingMainTabs = res.main_tabs.filter(function (tab) {
				// console.log(tab, prefix, ~tab.indexOf(prefix));
				return ~tab.indexOf(prefix);
			});
			console.log(matchingMainTabs);
			
			let main_tabs = matchingMainTabs.map(function(key){
				return res.tabs[key].tab;
			}).join('');
			let main_contents = matchingMainTabs.map(function (key) {
				return res.tabs[key].content;
			}).join('');


			let matchingElseTabs = res.else_tabs.filter(function (tab) {
				return ~tab.indexOf(prefix);
			});
			console.log(matchingElseTabs);

			let else_tabs = matchingElseTabs.map(function (key) {
				return res.tabs[key].tab;
			}).join('');
			if (else_tabs.length > 0 && main_tabs.length > 0) {
				console.log(typeof(else_tabs));
				else_tabs = '<hr role="separator" class="divider border" />' + else_tabs;
			}
			let contents = main_contents.concat( matchingElseTabs.map(function (key) {
				return res.tabs[key].content;
			}).join(''));
			
			let scripts = get_scripts();
			let css = scripts[0];
			let js = scripts[1];
			let html = res.body
				.replace('MAINTABS', main_tabs)
				.replace('ELSETABS', else_tabs)
				.replace('CONTENTS', contents)
				.replace('EXTERNAL_CSS', css)
				.replace('EXTERNAL_JS', js);
			
			panel.webview.html = html;
		}

		rp(options)
			.then(function (res) { // res
				let filter_by_prefix = '';
				const panel = window.createWebviewPanel(
					'tips', // Identifies the type of the webview. Used internally
					'Подсказка jQuery', // Title of the panel displayed to the user
					vscode.ViewColumn.Two, // Editor column to show the new webview panel in.
					{
						// Enable scripts in the webview
						enableScripts: true,
						localResourceRoots: [vscode.Uri.file(path.join(context.extensionPath, 'assets'))]
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
									// console.log(newRes);
									res.tabs = newRes.tabs;
									res.main_tabs =newRes.main_tabs;
									res.else_tabs = newRes.else_tabs;
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
