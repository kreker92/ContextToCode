"use strict";

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


const fields = $('.form-edit').find('input, select, textarea, radio');

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