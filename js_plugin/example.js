 // set to enter state before appending
 classList.add(enterClass)
 // append
 changeState()
 // trigger transition
 if (!hasAnimation) {
	 batcher.push({
		 execute: function () {
			 classList.remove(enterClass)
		 }
	 })
 } else {
	 onEnd = function (e) {
		 if (e.target === el) {
			 el.removeEventListener(endEvent, onEnd)
			 el.vue_trans_cb = null
			 classList.remove(enterClass)
		 }
	 }
	 //el.addEventListener(endEvent, onEnd)
     //el.vue_trans_cb = onEnd 
 }
