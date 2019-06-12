
const fs = require('fs')

var spawn = require('child_process').spawn;


const list = fs.readFileSync('repolist.txt', 'utf-8').split('\n')

console.log(list);

const clone = i => {

  spawn('git', ['clone', list[i]]).on('close', code=>{

    console.log(list[i]+` cloned (${code})`)
  })

  setTimeout(()=>clone(i+1), 5000)

}

process.chdir('repos');

clone(0)