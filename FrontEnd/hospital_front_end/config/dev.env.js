'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  // BASE_API: '"https://easy-mock.com/mock/5950a2419adc231f356a6636/vue-admin"',
  // BASE_API: '"http://localhost:8202"',

  // mac版本
  BASE_API: '"http://10.211.55.4:8000"',

  // win版本
  // BASE_API: '"http://192.168.140.128:8000"',

})
