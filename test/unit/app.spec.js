describe('AppCtrl', function ()
{
    'use strict';

    var controller;

    beforeEach(module('app'));

    function createController($controller)
    {
        controller = $controller('AppCtrl');
    }

    describe('constructor', function ()
    {
        beforeEach(inject(function ($controller)
        {
            createController($controller);
        }));

        it('should load message', function ()
        {
            expect(controller.message).toEqual('Hello Angular App!');
        });
    });
});
