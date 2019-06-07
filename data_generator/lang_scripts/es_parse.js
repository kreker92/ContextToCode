#!/usr/bin/env node
'use strict';

const fs = require('fs')
const parse = require('acorn-loose').parse

const file = process.argv[2]

const result = parse(fs.readFileSync(file, "utf8"))

console.log(JSON.stringify(result))

