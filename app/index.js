(function ()
{
  'use strict';
  var http = require('http');
  http.createServer(function (request, respond)
  {
    respond.writeHead(200);
    respond.end()
  }).listen(3000)
})();
