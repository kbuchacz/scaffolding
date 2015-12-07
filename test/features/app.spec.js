var App = require('./pageFragments/app.fragment.js');
var app = new App();

describe('Sample application', function ()
{
    'use strict';

    beforeAll(function ()
    {
        browser.get('/');
    });
    describe('initialization', function ()
    {
        it('should display message in header', function ()
        {
            expect(app.getHeaderValue()).toEqual('Angular exercise');
        });
    });
});
