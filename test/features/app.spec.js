'use strict';

var SampleApplication = require('./pageFragments/sampleApplication.fragment.js');
var sampleApplication = new SampleApplication();

describe('Sample application', function ()
{

    beforeAll(function ()
    {
        browser.get('/');
    });
    describe('initialization', function ()
    {
        it('should display message in header', function ()
        {
            expect(sampleApplication.getHeaderValue()).toEqual('Angular exercise');
        });
    });
});
