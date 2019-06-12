

const fs = require('fs')
const GitHub = require('github-api');

// unauthenticated client
const gh = new GitHub()

const resp = gh.search({q:'app',in:'readme',language:'javascript', sort:'stars',order:'desc'}).forRepositories().then(resp => {


  fs.writeFileSync('repolist.txt', resp.data.map(el=>el.ssh_url).join('\n'));
}).catch(e=>{

  console.log(e);
  console.log(gh);
})
