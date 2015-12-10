/// <reference path="typings/references.d.ts" />
var ng;
(function (ng) {
    (function (jsplumb) {
        var jsPlumbModule = angular.module('jsplumb', []);

        jsPlumbModule.service('jsplumb.customOverlay', [
            '$compile', '$timeout',
            function ($compile, $timeout) {
                return function (scope, overlayOptionsArray) {
                    if (!angular.isArray(overlayOptionsArray))
                        return;

                    overlayOptionsArray.forEach(function (overlay) {
                        if (!angular.isArray(overlay))
                            return;
                        var overlayType = overlay[0];
                        if (overlayType != 'Custom')
                            return;
                        var overlayOptions = overlay[1];

                        if (overlayOptions.templateUrl) {
                            var templateUrl = overlayOptions.templateUrl;
                            overlayOptions.create = function (connection) {
                                var overlayElement = angular.element(document.createElement('div')).attr('ng-include', "'" + overlayOptions.templateUrl + "'");
                                overlayElement = angular.element(document.createElement('div')).append(overlayElement);

                                var overlayScope = scope.$new();

                                $timeout(function () {
                                    // Do after timeout to get a $$model
                                    angular.extend(overlayScope, {
                                        $connection: connection.$$model
                                    });

                                    $compile(overlayElement.contents())(overlayScope);
                                });
                                return overlayElement;
                            };
                        }
                    });
                };
            }]);

        var JsPlumbController = (function () {
            function JsPlumbController($scope, customOverlay) {
                var _this = this;
                this.$scope = $scope;
                this.customOverlay = customOverlay;
                this.endpoints = [];
                this.connections = [];
                this.pendingConnections = [];
                var $apply = function (fn) {
                    return function (info, originalEvent) {
                        if (originalEvent != null) {
                            $scope.$apply(function () {
                                fn.apply(_this, [info, originalEvent]);
                            });
                        } else {
                            fn.apply(_this, [info, originalEvent]);
                        }
                    };
                };

                jsPlumb.ready(function () {
                    _this.instance = jsPlumb.getInstance();
                    _this.initialized = true;
                    _this.instance.bind('connection', $apply(_this.onConnection));
                    _this.instance.bind('connectionDetached', $apply(_this.onConnectionDetached));
                    _this.instance.bind('connectionMoved', $apply(_this.onConnectionMoved));
                    // see: http://jsplumbtoolkit.com/doc/events
                });
            }
            JsPlumbController.prototype.onConnection = function (info, originalEvent) {
                // Only if from UI
                if (originalEvent != null) {
                    var connection = info.connection;

                    // If moved, don't handle
                    if (connection.$$model)
                        return;
                    var connectionModel = {
                        source: info.sourceEndpoint.$$model,
                        target: info.targetEndpoint.$$model
                    };
                    connection.$$model = connectionModel;
                    this.connections.push(connection);
                }

                this.$scope.$broadcast('$$jsplumbConnection', info, originalEvent);
            };

            JsPlumbController.prototype.onConnectionDetached = function (info, originalEvent) {
                // Only if from UI
                if (originalEvent != null) {
                    var connection = info.connection;
                    var index = this.connections.indexOf(connection);
                    this.connections.splice(index, 1);
                }

                this.$scope.$broadcast('$$jsplumbConnectionDetached', info, originalEvent);
            };

            JsPlumbController.prototype.onConnectionMoved = function (info, originalEvent) {
                var connectionModel = info.connection.$$model;
                var newSourceModel = info.newSourceEndpoint.$$model;
                var newTargetModel = info.newTargetEndpoint.$$model;
                connectionModel.source = newSourceModel;
                connectionModel.target = newTargetModel;

                this.$scope.$broadcast('$$jsplumbConnectionMoved', info, originalEvent);
            };

            JsPlumbController.prototype.destroy = function () {
                this.instance.reset();
                this.instance = null;
                this.destroyed = true;
            };

            JsPlumbController.prototype.updatePosition = function (element) {
                if (!this.initialized || this.destroyed)
                    return;

                this.instance.repaint(element[0]);
            };

            JsPlumbController.prototype.syncConnections = function (connectionModels) {
                var _this = this;
                // Remove deleted connections:
                var removedConnections = this.connections.filter(function (connection) {
                    return !connectionModels.some(function (connectionModel) {
                        return connectionModel == connection.$$model;
                    });
                });

                removedConnections.forEach(this.unconnect.bind(this));

                // Add missing connections
                connectionModels.forEach(function (connectionModel) {
                    _this.connect(connectionModel);
                });
            };

            JsPlumbController.prototype.unconnect = function (connection) {
                var index = this.connections.indexOf(connection);
                this.instance.detach(connection);
                this.connections.splice(index, 1);
            };

            JsPlumbController.prototype.processPendingConnections = function () {
                var pendingConnections = this.pendingConnections;
                this.pendingConnections = [];
                pendingConnections.forEach(this.connect.bind(this));
            };

            JsPlumbController.prototype.connect = function (connectionModel) {
                var connection = this.connections.filter(function (connection) {
                    return connection.$$model == connectionModel;
                })[0];

                if (connection)
                    return connection;

                var sourceEndpoint = this.endpoints.filter(function (endpoint) {
                    return endpoint.$$model == connectionModel.source;
                })[0];
                var targetEndpoint = this.endpoints.filter(function (endpoint) {
                    return endpoint.$$model == connectionModel.target;
                })[0];

                var connectionOptions = connectionModel.options || {};
                this.customOverlay(this.$scope, connectionOptions.overlays);

                if (!sourceEndpoint || !targetEndpoint) {
                    this.pendingConnections.push(connectionModel);
                    return null;
                }

                var connection = this.instance.connect(angular.extend({
                    source: sourceEndpoint,
                    target: targetEndpoint
                }, connectionOptions));

                this.connections.push(connection);

                connection['$$model'] = connectionModel;
                return connection;
            };

            JsPlumbController.prototype.addEndpoint = function (element, options) {
                var _this = this;
                var endpoint = this.instance.addEndpoint(element, options);
                endpoint.$$model = options.model;
                this.endpoints.push(endpoint);

                angular.element(element).scope().$on('destroy', function () {
                    _this.instance.deleteEndpoint(endpoint);
                });

                this.processPendingConnections();

                return endpoint;
            };

            JsPlumbController.prototype.removeEndpoint = function (endpoint) {
                endpoint.connections.forEach(this.unconnect.bind(this));

                this.instance.deleteEndpoint(endpoint);
                var index = this.endpoints.indexOf(endpoint);
                this.endpoints.splice(index, 1);
            };
            JsPlumbController.$inject = ['$scope', 'jsplumb.customOverlay'];
            return JsPlumbController;
        })();

        jsPlumbModule.directive('jsplumb', [
            '$parse', function ($parse) {
                return {
                    restrict: 'A',
                    controller: JsPlumbController,
                    link: function (scope, element, attrs, jsPlumbCtrl) {
                        var onConnection = angular.noop;
                        if (attrs.onConnection)
                            onConnection = $parse(attrs.onConnection);
                        var onConnectionDetached = angular.noop;
                        if (attrs.onConnectionDetached)
                            onConnectionDetached = $parse(attrs.onConnectionDetached);
                        var onConnectionMoved = angular.noop;
                        if (attrs.onConnectionMoved)
                            onConnectionMoved = $parse(attrs.onConnectionMoved);

                        var defaults = angular.extend({
                            Container: element
                        }, scope.$eval(attrs.jsplumbDefaults));

                        jsPlumb.ready(function () {
                            jsPlumbCtrl.instance.importDefaults(defaults);

                            scope.$on('$$jsplumbConnection', function (event, info, originalEvent) {
                                // If created from API, don't handle
                                if (originalEvent == null)
                                    return;

                                var sourceElement = info.source;
                                var targetElement = info.target;
                                var $sourceScope = angular.element(sourceElement).scope();
                                var $targetScope = angular.element(targetElement).scope();

                                onConnection(scope, {
                                    $source: info.sourceEndpoint.$$model,
                                    $target: info.targetEndpoint.$$model,
                                    $connection: info.connection.$$model,
                                    $sourceScope: $sourceScope,
                                    $targetScope: $targetScope
                                });
                            });

                            scope.$on('$$jsplumbConnectionDetached', function (event, info, originalEvent) {
                                // If created from API, don't handle
                                if (originalEvent == null)
                                    return;

                                var sourceElement = info.source;
                                var targetElement = info.target;
                                var $sourceScope = angular.element(sourceElement).scope();
                                var $targetScope = angular.element(targetElement).scope();

                                onConnectionDetached(scope, {
                                    $source: info.sourceEndpoint.$$model,
                                    $target: info.targetEndpoint.$$model,
                                    $connection: info.connection.$$model,
                                    $sourceScope: $sourceScope,
                                    $targetScope: $targetScope
                                });
                            });

                            scope.$on('$$jsplumbConnectionMoved', function (event, info, originalEvent) {
                                // If created from API, don't handle
                                if (originalEvent == null)
                                    return;

                                var sourceElement = info.source;
                                var targetElement = info.target;
                                var $sourceScope = angular.element(sourceElement).scope();
                                var $targetScope = angular.element(targetElement).scope();

                                onConnectionMoved(scope, {
                                    $originalSource: info.originalSourceEndpoint.$$model,
                                    $originalTarget: info.originalTargetEndpoint.$$model,
                                    $source: info.newSourceEndpoint.$$model,
                                    $target: info.newTargetEndpoint.$$model,
                                    $connection: info.connection.$$model,
                                    $sourceScope: $sourceScope,
                                    $targetScope: $targetScope
                                });
                            });
                        });

                        scope.$on('$destroy', function () {
                            jsPlumbCtrl.destroy();
                            jsPlumbCtrl = null;
                        });
                    }
                };
            }]);

        jsPlumbModule.directive('jsplumbDraggable', [
            '$parse', function ($parse) {
                return {
                    restrict: 'A',
                    scope: false,
                    require: '^jsplumb',
                    link: function (scope, element, attrs, jsPlumbCtrl) {
                        var draggableOptions = angular.extend({}, scope.$eval(attrs.jsplumbDraggable));

                        if (attrs.left) {
                            scope.$watch(attrs.left, function (leftOffset) {
                                leftOffset = leftOffset || 0;
                                element[0].style.left = leftOffset + 'px';
                                jsPlumbCtrl.updatePosition(element);
                            });
                        }

                        if (attrs.top) {
                            scope.$watch(attrs.top, function (topOffset) {
                                topOffset = topOffset || 0;
                                element[0].style.top = topOffset + 'px';
                                jsPlumbCtrl.updatePosition(element);
                            });
                        }

                        if (attrs.left || attrs.top) {
                            var leftSetter = (attrs.left && $parse(attrs.left).assign) || angular.noop;
                            var topSetter = (attrs.top && $parse(attrs.top).assign) || angular.noop;

                            var _start = draggableOptions.start || angular.noop;
                            draggableOptions.start = function (event, ui) {
                                jsPlumbCtrl.instance.recalculateOffsets(element[0]);
                                _start.apply(this, arguments);
                            };

                            var _stop = draggableOptions.stop || angular.noop;
                            
                            //
                            // pkr: Changed because it had a ui object that seems
                            // to have come from jQuery. Adapted to  use
                            // the position from the event
                            //
                            
                            draggableOptions.stop = function (event) {
                                scope.$apply(function () {
                               		leftSetter(scope, event.pos[0]);
                               		topSetter(scope, event.pos[1]);
                                });
                                _stop.apply(this, arguments);
                            };
                        }

                        jsPlumb.ready(function () {
                            jsPlumbCtrl.instance.draggable(element, draggableOptions);
                        });
                    }
                };
            }]);

        jsPlumbModule.directive('jsplumbEndpoint', [
            '$parse', 'jsplumb.customOverlay',
            function ($parse, customOverlay) {
                return {
                    restrict: 'A',
                    scope: false,
                    require: ['^jsplumb'],
                    link: function (scope, element, attrs, ctrls) {
                        var jsPlumbCtrl = ctrls[0];
                        var endpoints = [];

                        var onConnection = angular.noop;
                        if (attrs.onConnection)
                            onConnection = $parse(attrs.onConnection);
                        var onConnectionDetached = angular.noop;
                        if (attrs.onConnectionDetached)
                            onConnectionDetached = $parse(attrs.onConnectionDetached);
                        var onConnectionMoved = angular.noop;
                        if (attrs.onConnectionMoved)
                            onConnectionMoved = $parse(attrs.onConnectionMoved);

                        var endpointOptionsArray = scope.$eval(attrs.jsplumbEndpoint) || {};
                        if (!angular.isArray(endpointOptionsArray)) {
                            endpointOptionsArray = [endpointOptionsArray];
                        }
                        endpointOptionsArray.forEach(function (endpointOptions) {
                            // Custom overlays
                            customOverlay(scope, endpointOptions.connectorOverlays);

                            jsPlumb.ready(function () {
                                var endpoint = jsPlumbCtrl.addEndpoint(element, endpointOptions);
                                endpoints.push(endpoint);

                                scope.$on('$$jsplumbConnection', function (event, info, originalEvent) {
                                    // If created from API, don't handle
                                    if (originalEvent == null)
                                        return;

                                    // If endpoint used
                                    if (info.sourceEndpoint != endpoint && info.targetEndpoint != endpoint)
                                        return;

                                    var sourceElement = info.source;
                                    var targetElement = info.target;
                                    var $sourceScope = angular.element(sourceElement).scope();
                                    var $targetScope = angular.element(targetElement).scope();

                                    onConnection(scope, {
                                        $source: info.sourceEndpoint.$$model,
                                        $target: info.targetEndpoint.$$model,
                                        $connection: info.connection.$$model,
                                        $sourceScope: $sourceScope,
                                        $targetScope: $targetScope
                                    });
                                });

                                scope.$on('$$jsplumbConnectionDetached', function (event, info, originalEvent) {
                                    // If created from API, don't handle
                                    if (originalEvent == null)
                                        return;

                                    // If endpoint used
                                    if (info.sourceEndpoint != endpoint && info.targetEndpoint != endpoint)
                                        return;

                                    var sourceElement = info.source;
                                    var targetElement = info.target;
                                    var $sourceScope = angular.element(sourceElement).scope();
                                    var $targetScope = angular.element(targetElement).scope();

                                    onConnectionDetached(scope, {
                                        $source: info.sourceEndpoint.$$model,
                                        $target: info.targetEndpoint.$$model,
                                        $connection: info.connection.$$model,
                                        $sourceScope: $sourceScope,
                                        $targetScope: $targetScope
                                    });
                                });

                                scope.$on('$$jsplumbConnectionMoved', function (event, info, originalEvent) {
                                    // If created from API, don't handle
                                    if (originalEvent == null)
                                        return;

                                    // If endpoint used
                                    if (info.newSourceEndpoint != endpoint && info.newTargetEndpoint != endpoint)
                                        return;

                                    var sourceElement = info.source;
                                    var targetElement = info.target;
                                    var $sourceScope = angular.element(sourceElement).scope();
                                    var $targetScope = angular.element(targetElement).scope();

                                    onConnectionMoved(scope, {
                                        $originalSource: info.originalSourceEndpoint.$$model,
                                        $originalTarget: info.originalTargetEndpoint.$$model,
                                        $source: info.newSourceEndpoint.$$model,
                                        $target: info.newTargetEndpoint.$$model,
                                        $connection: info.connection.$$model,
                                        $sourceScope: $sourceScope,
                                        $targetScope: $targetScope
                                    });
                                });
                            });
                        });

                        jsPlumb.ready(function () {
                            // Would be nice if angular $emitted something after changing DOM
                            setTimeout(function () {
                                jsPlumbCtrl.instance.repaint(element[0]);
                            }, 0);
                        });

                        // use element.on because in scope.$on('$destroy') the element is already removed
                        element.on('$destroy', function () {
                            endpoints.forEach(function (endpoint) {
                                jsPlumbCtrl.removeEndpoint(endpoint);
                            });
                        });
                    }
                };
            }]);

        jsPlumbModule.directive('jsplumbConnections', [
            '$parse', '$timeout', function ($parse, $timeout) {
                return {
                    restrict: 'A',
                    scope: false,
                    require: 'jsplumb',
                    link: function (scope, element, attrs, jsPlumbCtrl) {
                        var connectionsGetter = $parse(attrs.jsplumbConnections);

                        jsPlumb.ready(function () {
                            scope.$watchCollection(attrs.jsplumbConnections, function (connections) {
                                jsPlumbCtrl.syncConnections(connections);
                            });

                            scope.$on('$$jsplumbConnection', function (event, info, originalEvent) {
                                // Only if from UI
                                if (originalEvent == null)
                                    return;

                                var model = info.connection.$$model;
                                var connections = connectionsGetter(scope);
                                connections.push(model);
                            });

                            scope.$on('$$jsplumbConnectionDetached', function (event, info, originalEvent) {
                                var connections = connectionsGetter(scope);
                                var model = info.connection.$$model;
                                var connection = info.connection;
                                var index = connections.indexOf(model);
                                if (index != -1) {
                                    connections.splice(index, 1);
                                }
                            });
                        });
                    }
                };
            }]);
    })(ng.jsplumb || (ng.jsplumb = {}));
    var jsplumb = ng.jsplumb;
})(ng || (ng = {}));
//# sourceMappingURL=jsplumb-angular.js.map
