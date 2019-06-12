#!/usr/bin/env node
'use strict';

const fs = require('fs')
const parse = require('acorn-loose').parse

const filename = process.argv[2]

const result = parse(fs.readFileSync(filename, "utf-8"))

console.log(JSON.stringify(result))

