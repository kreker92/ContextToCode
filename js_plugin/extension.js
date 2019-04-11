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
      .then(function (htmlString1) {
				let htmlString = `
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
    </style>
  </head>
  <body class="bg-dark">
    <div class="container-fluid">
      <div class="tabs">
        <ul class="nav nav-tabs" role="tablist">
          <li class="nav-item">
            <a class="nav-link active" id="tip-1-tab" data-toggle="tab" href="#tip-1" role="tab" aria-controls="tip-1" aria-selected="true">jQuery.ajax</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" id="tip-2-tab" data-toggle="tab" href="#tip-2" role="tab" aria-controls="tip-2" aria-selected="false">alert</a>
          </li>
        </ul>
      </div>
      <div class="tab-content">
        <div class="tab-pane fade show active" id="tip-1" role="tabpanel" aria-labelledby="tip-1-tab" data-tip="1">
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
        <div class="tab-pane fade" id="tip-2" role="tabpanel" aria-labelledby="tip-2-tab" data-tip="2">
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

      var resCode = {
        tab1: {
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
        },
        tab2: {
          replace1: '$.ajax1({',
          replace2: '});;',
          template: \`  $.ajax1({
            url: PART1,
            success: PART2
          });;
          \`
        }
      };

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
      
    </script>
  </body>
</html>`;

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
