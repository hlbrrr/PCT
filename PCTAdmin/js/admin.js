/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/26/11
 * Time: 2:31 PM
 */
(function($, window) {
    $.extend(window, {
        PCT:{}
    });

    $.extend(PCT, {
        timeout:10000,
        animation:'blind',
        format:'xml',
        lockScreen : function() {
            if (!PCT.lockCounter) {
                $.extend(PCT, {
                    lockCounter : 1
                });
            } else {
                PCT.lockCounter++;
            }
            PCT.lockDiv.updateView();
            $(PCT.lockDiv).show();
        },
        unlockScreen : function() {
            PCT.lockCounter--;
            if (PCT.lockCounter <= 0) {
                PCT.lockCounter = 0;
                $(PCT.lockDiv).hide();
            }
        },
        preventDefault : function(e) {
            e.preventDefault();
            return false;
        },
        updateViewCallbacks : new Array(),
        updateView : function() {
            for (key in PCT.updateViewCallbacks) {
                PCT.updateViewCallbacks[key]();
            }
        },
        disableAnimation:function() {
            $.fx.off = true;
        },
        template:function(template) {
            $.extend(this, {
                dom:$('<div></div>').append($(template).contents()),
                getCopy:function() {
                    var retDom = this.dom.clone();
                    $('input.validate', retDom).bind('error',
                        function() {
                            $(this).addClass('error');
                            $(this).keypress(function(e) {
                                var keycode;
                                if (window.event)
                                    keycode = window.event.keyCode;
                                else if (e)
                                    keycode = e.which;
                                else
                                    return true;
                                if (keycode != 13) {
                                    $(this).trigger('unError');
                                }
                            });
                        }).bind('unError', function() {
                            $(this).removeClass('error');
                        });

                    $('input.validate', retDom).blur(
                        function(e) {
                            if (!PCT.validate(this)) {
                                PCT.mark(this);
                                e.stopImmediatePropagation();
                            }
                        }).change(function(e) {
                            if (!PCT.validate(this)) {
                                PCT.mark(this);
                                e.stopImmediatePropagation();
                            }
                        });
                    $('input.validate', retDom).change();
                    return retDom.contents();
                }
            });

        },
        addTemplate:function(template) {
            if (!this.templates) {
                $.extend(this, {
                    templates:new Array()
                })
            }
            this.templates[$(template).attr('id')] = new PCT.template(template);
            $(template).remove();
        },
        getTemplate:function(id) {
            if (this.templates && this.templates[id]) {
                return this.templates[id].getCopy();
            }
        },
        mark : function(item) {
            $(item).trigger('error');
            retValue = false;
        },
        unMark : function(item) {
            $(item).trigger('unError');
        },
        isDate:function  (day, month, year) {
            try {
                day = Number(day);
                month = Number(month);
                year = Number(year);
            } catch(e) {
                return false;
            }
            month--;
            var test = new Date(year, month, day);
            if ((test.getFullYear() == year) &&
                (month == test.getMonth()) &&
                (day == test.getDate()))
                return true;
            else
                return false;
        },
        validate:function(obj) {
            var elem = $(obj);
            PCT.unMark(elem)
            var validclass = elem.attr('validclass') ? elem.attr('validclass') : '';

            if (validclass.indexOf('mempty') != -1 && elem.val() == '') {
                return true;
            }
            if (validclass.indexOf('mempty') == -1 && elem.val() == '') {
                return false;
            }
            if (elem.val().indexOf('>') != -1 || elem.val().indexOf('<') != -1 || elem.val().indexOf('&') != -1) {
                return false;
            }
            if (validclass.indexOf('unique') != -1) {
                var unique = elem.attr('unique');
                var uniqueroot = elem.attr('uniqueroot');
                var elVal = elem.val();
                var counter = 0;
                $(unique, elem.parents(uniqueroot)).each(function() {
                    if ($(this).val() == elVal) {
                        counter++;
                    }
                });
                return counter < 2;
            }
            if (validclass.indexOf('summ') != -1) {
                if (isNaN(elem.val())) {
                    return false;
                } else {
                    var minval = elem.attr('minval');
                    var maxval = elem.attr('maxval');
                    var elVal = Number(elem.val());
                    var bl = elVal <= 0;
                    if (minval && minval != '' && !isNaN(minval)) {
                        bl = elVal < minval;
                    }
                    if (maxval && maxval != '' && !isNaN(maxval)) {
                        bl = bl || elVal > maxval;
                    }
                    if (bl) {
                        return false;
                    }
                }
            }
            if (validclass.indexOf('int') != -1) {
                if (isNaN(elem.val())) {
                    return false;
                } else {
                    var maxval = elem.attr('maxval');
                    var minval = elem.attr('minval');
                    var elVal = Number(elem.val());
                    var bl = false;
                    if (minval && minval != '' && !isNaN(minval)) {
                        bl = elVal < minval;
                    }
                    if (maxval && maxval != '' && !isNaN(maxval)) {
                        bl = bl || (elVal > maxval);
                    }
                    if (bl) {
                        return false;
                    }
                }
            }
            if (validclass.indexOf('date') != -1) {
                var bl = false;
                var elVal = elem.val();
                var maxval = elem.datepicker('option', 'maxDate');
                var minval = elem.datepicker('option', 'minDate');

                var dateArr = elVal.split('/');
                if (dateArr.length == 3 && PCT.isDate(dateArr[0], dateArr[1], dateArr[2])) {
                    var dateTest = new Date(dateArr[2], Number(dateArr[1]) - 1, dateArr[0]);
                    if (maxval) {
                        maxval.setHours(0);
                        maxval.setMinutes(0);
                        maxval.setSeconds(0);
                        maxval.setMilliseconds(0);
                        if (dateTest.getTime() > maxval.getTime()) {
                            bl = true;
                        }
                    }
                    if (minval) {
                        minval.setHours(0);
                        minval.setMinutes(0);
                        minval.setSeconds(0);
                        minval.setMilliseconds(0);
                        if (dateTest.getTime() < minval.getTime()) {
                            bl = true;
                        }
                    }
                } else {
                    bl = true;
                }
                if (bl) {
                    return false;
                }
            }

            return true;
        }
    });
    $.extend(PCT, {
        init:function(templatesContainer, root) {
            PCT.disableAnimation();
            $('template', templatesContainer).each(function() {
                PCT.addTemplate(this);
            });
            {
                var lockDivTemplate = PCT.getTemplate('locker');
                var lockRoot = $('#lockDiv', lockDivTemplate);
                lockRoot.bind("touchmove", PCT.preventDefault);
                lockRoot.bind("touchstart", PCT.preventDefault);
                lockRoot.bind("touchend", PCT.preventDefault);
                lockRoot.bind("click", PCT.preventDefault);
                $.extend(lockDivTemplate, {
                    updateView : function() {
                        var pageHeight = $('body').height();
                        lockRoot.height(pageHeight > window.innerHeight ? pageHeight : innerHeight);
                        $('.animation', lockRoot).css('top', window.pageYOffset + window.innerHeight / 3);
                    }
                });
                root.append(lockDivTemplate);

                $.extend(PCT, {
                    lockDiv : lockDivTemplate
                });

                $(window).bind('scroll', PCT.lockDiv.updateView);
                PCT.updateViewCallbacks.push(PCT.lockDiv.updateView);
            }
            if (window.orientation != undefined) {
                window.onorientationchange = PCT.updateView;
            }
            $(window).resize(PCT.updateView);
            PCT.lockScreen();
            $.ajax({
                url:'data',
                data:{
                    action:'getConfig',
                    format:PCT.format
                },
                dataType:PCT.format,
                success:function(data, textStatus, jqXHR) {
                    PCT.tryToInit(data, root);
                },
                error:function(jqXHR, textStatus, errorThrown) {
                    PCT.initError();
                },
                complete:function() {
                    PCT.unlockScreen();
                }
            });
        },
        tryToInit:function(initialData, root) {
            $.extend(PCT, {
                currentModel:(new PCT.model()).init($('root', initialData)).setRoot(root)
            });
        },
        initError:function() {
            alert('Initialization failed');
        },
        sendData:function (url, data, method) {
            if (url && data) {
                var inputs = '';
                $.each(data.split('&'), function() {
                    var pair = this.split('=');
                    inputs += '<input type="hidden" name="' + pair[0] + '" value="' + pair[1] + '" />';
                });
                $('<form action="' + url + '" method="' + (method || 'post') + '">' + inputs + '</form>')
                    .appendTo('body').submit().remove();
            }
        }
    });

    $.extend(PCT, {
        base:{
            setRoot : function(root) {
                if (!root) {
                    root = $('<div></div>');
                }
                $(root).append(this.root.contents());
                this.root = root;
                return this;
            },
            getRoot:function() {
                return this.root;
            }
        }
    });

    $.extend(PCT, {
        model:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("model");
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _expiration:$('#Expiration', dom).get(),
                _products:$('#Products', dom).get(),
                _addProduct:$('#AddProduct', dom).get(),
                _saveConfiguration:$('#SaveConfiguration', dom).get(),
                _downloadConfiguration:$('#DownloadConfiguration', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $(this._addProduct).click(function() {
                that.addProduct();
            });
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<root>';
                    config += '<Expiration>' + $(that._expiration).val() + '</Expiration>';
                    config += '<Products>';
                    $(that._products).children().each(function() {
                        if ($(this).hasClass('divProduct'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Products>';
                    config += '</root>';
                    return config;
                }
            });
            $(this._downloadConfiguration).click(function() {
                PCT.sendData('/data', 'action=downloadConfig');
            });
            $(this._saveConfiguration).click(function() {
                if ($('.error', this._products).length > 0) {
                    alert('There are some errors in configuration. Fix them first, then try again.');
                    return;
                }
                var message = prompt("Describe configuration changes that you made", "");
                if (message == null)
                    return;
                PCT.lockScreen();
                var config = $.data($(that._core)[0], 'pct').getXML();
                $.ajax({
                    url:'data',
                    data:{
                        action:'saveConfig',
                        config:config,
                        message:message
                    },
                    success:function(data, textStatus, jqXHR) {
                        alert('Configuration saved');
                    },
                    error:function(jqXHR, textStatus, errorThrown) {
                        alert('Configuration can\'t be saved');
                    },
                    complete:function() {
                        PCT.unlockScreen();
                    }
                });
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                init:function(initialData) {
                    $(this._expiration).datepicker({
                        dateFormat:'dd/mm/yy',
                        minDate:new Date(),
                        showAnim: PCT.animation,
                        onSelect:function() {
                            PCT.unMark($(this));
                        }
                    });
                    $(this._expiration).val($('>Expiration', initialData).text());
                    var that = this;
                    $('>Products>Product', initialData).each(function() {
                        that.addProduct((new PCT.product()).init(this));
                    });
                    return this;
                },
                addProduct:function(product) {
                    if (product) {
                        product.setRoot(this._products);
                        $('html, body').animate({
                            scrollTop: $(product.getHead()).offset().top
                        }, 200);
                    } else {
                        this.addProduct((new PCT.product()).addCapacitiesRoot().addModulesRoot());
                    }
                }
            });
        },
        product:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("product");
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _maximumFunctionalityPrice:$('#MaximumFunctionalityPrice', dom).get(),
                _minimumPrice:$('#MinimumPrice', dom).get(),
                _content:$('#Content', dom).get(),
                _modules:$('#Modules', dom).get(),
                _capacities:$('#Capacities', dom).get(),
                _productTitle:$('#ProductTitle', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Product>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                    config += '<MaximumFunctionalityPrice>' + $(that._maximumFunctionalityPrice).val() + '</MaximumFunctionalityPrice>';
                    config += '<MinimumPrice>' + $(that._minimumPrice).val() + '</MinimumPrice>';
                    $(that._modules).children().each(function() {
                        if ($(this).hasClass('divModulesGroup'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    $(that._capacities).children().each(function() {
                        if ($(this).hasClass('divCapacitiesGroup'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Product>';
                    return config;
                }
            });
            $(this._remove).click(
                function() {
                    if (confirm('Remove product?')) {
                        if (confirm('Removing product can result in broken backward compatibility. Remove product?')) {
                            $(that._body).remove();
                        }
                    }
                });
            $(this._expand).click(
                function(arg) {
                    $(that._content).toggle(PCT.animation, function() {
                        if ($(this).css('display') == 'none') {
                            $(that._expand).html('Expand');
                        } else {
                            $(that._expand).html('Collapse');
                        }
                    });
                });

            $(this._name).change(function() {
                $(that._productTitle).html($(this).val());
            });
            $(this._productTitle).click(
                function() {
                    $(that._remove).toggleClass('hidden');
                    $(that._settingsPane).toggleClass('hidden');
                });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._shortName).val($('>ShortName', initialData).text()).change();
                    $(this._maximumFunctionalityPrice).val($('>MaximumFunctionalityPrice', initialData).text()).change();
                    $(this._minimumPrice).val($('>MinimumPrice', initialData).text()).change();
                    this.addModulesRoot((new PCT.modulesGroup()).init(null, $('>Modules', initialData)));
                    this.addCapacitiesRoot((new PCT.capacitiesGroup()).init(null, $('>Capacities', initialData)));
                    $(this._settingsPane).addClass('hidden');
                    $(this._content).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    $(this._expand).html('Expand');
                    return this;
                },
                addModulesRoot:function(modulesGroup) {
                    if (modulesGroup) {
                        modulesGroup.setIsRoot(true).setRoot(this._modules);
                    } else {
                        this.addModulesRoot((new PCT.modulesGroup()).setIsRoot(true));
                    }
                    return this;
                },
                addCapacitiesRoot:function(capacitiesGroup) {
                    if (capacitiesGroup) {
                        capacitiesGroup.setIsRoot(true).setRoot(this._capacities);
                    } else {
                        this.addCapacitiesRoot((new PCT.capacitiesGroup()).setIsRoot(true));
                    }
                    return this;
                }
            });
        },
        modulesGroup:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("modulesGroup");
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _content:$('#Content', dom).get(),
                _modules:$('#Modules', dom).get(),
                _groups:$('#Groups', dom).get(),
                _isRoot:$('#IsRoot', dom).get(),
                _groupTitle:$('#GroupTitle', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _addGroup:$('#AddGroup', dom).get(),
                _addModule:$('#AddModule', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '';
                    if (!that.getIsRoot()) {
                        config += '<Group>';
                        config += '<Name>' + $(that._name).val() + '</Name>';
                        config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                    }
                    config += '<Modules>';
                    $(that._modules).children().each(function() {
                        if ($(this).hasClass('divModule'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    $(that._groups).children().each(function() {
                        if ($(this).hasClass('divModulesGroup'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Modules>';
                    if (!that.getIsRoot()) {
                        config += '</Group>';
                    }
                    return config;
                }
            });
            $(this._remove).click(
                function() {
                    if (confirm('Remove group?')) {
                        if (confirm('Group will be removed with nested modules. Removing module can result in broken backward compatibility. Remove group?')) {
                            $(that._body).remove();
                        }
                    }
                });
            $(this._expand).click(
                function(arg) {
                    $(that._content).toggle(PCT.animation, function() {
                        if ($(this).css('display') == 'none') {
                            $(that._expand).html('Expand');
                        } else {
                            $(that._expand).html('Collapse');
                        }
                    });
                });
            $(this._addGroup).click(function() {
                if ($(that._content).css('display') == 'none') {
                    $(that._content).show();
                    $(that._expand).html('Collapse');
                }
                that.addGroup();
            });
            $(this._addModule).click(function() {
                if ($(that._content).css('display') == 'none') {
                    $(that._content).show();
                    $(that._expand).html('Collapse');
                }
                that.addModule();
            });
            $(this._name).change(function() {
                $(that._groupTitle).html($(this).val());
            });
            $(this._isRoot).change(function() {
                if ($(this).val() == 'true') {
                    $(that._name).val('MR').change();
                    $(that._content).addClass('hidden');
                    $(that._expand).html('Expand');
                    $(that._settings).hide();
                    $(that._groupTitle).html('Modules Root');
                    $(that._settingsPane).addClass('hidden');
                    $(that._remove).addClass('hidden');
                    $(that._groupTitle).addClass('unclickable');
                } else {
                    $(that._groupTitle).click(
                        function() {
                            $(that._remove).toggleClass('hidden');
                            $(that._settingsPane).toggleClass('hidden');
                        });
                }
            });

            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                setIsRoot:function(r) {
                    $(this._isRoot).val(r ? 'true' : false).change();
                    return this;
                },
                getIsRoot:function() {
                    return $(this._isRoot).val() == 'true';
                },
                init:function(name, initialData) {
                    if (name) {
                        $(this._name).val(name).change();
                    }
                    var that = this;
                    $(this._shortName).val($('>ShortName', initialData).text()).change();
                    $('>Module', initialData).each(function() {
                        that.addModule((new PCT.module()).init(this));
                    });
                    $('>Group', initialData).each(function() {
                        that.addGroup((new PCT.modulesGroup()).init($('>Name', this).text(), $('>Modules', this)).setIsRoot(false));
                    });
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                },
                addModule:function(module) {
                    if (module) {
                        module.setRoot(this._modules);
                        $('html, body').animate({
                            scrollTop: $(module.getHead()).offset().top
                        }, 200);
                    } else {
                        this.addModule(new PCT.module());
                    }
                },
                addGroup:function(group) {
                    if (group) {
                        group.setRoot(this._groups);
                        $('html, body').animate({
                            scrollTop: $(group.getHead()).offset().top
                        }, 200);
                    } else {
                        this.addGroup((new PCT.modulesGroup()).setIsRoot(false));
                    }
                }
            });
        },
        module:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("module");
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _weight:$('#Weight', dom).get(),
                _key:$('#Key', dom).get(),
                _secondarySalesPrice:$('#SecondarySalesPrice', dom).get(),
                _secondarySalesRate:$('#SecondarySalesRate', dom).get(),
                _deprecated:$('#Deprecated', dom).get(),
                _moduleTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _dependencies:$('#Dependencies', dom).get(),
                _addDependency:$('#AddDependency', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Module>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                    config += '<Weight>' + $(that._weight).val() + '</Weight>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<SecondarySales>';
                    config += '<Price>' + $(that._secondarySalesPrice).val() + '</Price>';
                    config += '<Rate>' + $(that._secondarySalesRate).val() + '</Rate>';
                    config += '</SecondarySales>';
                    config += '<Deprecated>' + ($(that._deprecated).prop('checked') ? 'true' : 'false') + '</Deprecated>';
                    config += '<Dependencies>';
                    $(that._dependencies).children().each(function() {
                        if ($(this).hasClass('requireDependency'))
                            config += '<Require>' + $.data($(this)[0], 'pct').getXML() + '</Require>';
                    });
                    $(that._dependencies).children().each(function() {
                        if ($(this).hasClass('excludeDependency'))
                            config += '<Exclude>' + $.data($(this)[0], 'pct').getXML() + '</Exclude>';
                    });
                    config += '</Dependencies>';
                    config += '</Module>';
                    return config;
                }
            });
            $(this._remove).click(
                function() {
                    if (confirm('Remove module?')) {
                        if (confirm('Removing module can result in broken backward compatibility. Remove module?')) {
                            $(that._body).remove();
                        }
                    }
                });
            $(this._name).change(function() {
                $(that._moduleTitle).html($(this).val());
            });
            $(this._addDependency).click(function() {
                if ($(that._dependencies).css('display') == 'none') {
                    $(that._dependencies).show();
                    $(that._expand).html('Collapse');
                }
                that.addDependency();
            });
            $(this._deprecated).change(function() {
                if ($(this).prop('checked')) {
                    $(that._moduleTitle).addClass('deprecated');
                } else {
                    $(that._moduleTitle).removeClass('deprecated');
                }
            });
            $(this._moduleTitle).click(
                function() {
                    $(that._remove).toggleClass('hidden');
                    $(that._settingsPane).toggleClass('hidden');
                });
            $(this._expand).click(
                function(arg) {
                    $(that._dependencies).toggle(PCT.animation, function() {
                        if ($(this).css('display') == 'none') {
                            $(that._expand).html('Expand');
                        } else {
                            $(that._expand).html('Collapse');
                        }
                    });
                });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._shortName).val($('>ShortName', initialData).text()).change();
                    $(this._weight).val($('>Weight', initialData).text()).change();
                    $(this._key).val($('>Key', initialData).text()).change();
                    $(this._secondarySalesPrice).val($('>SecondarySales>Price', initialData).text()).change();
                    $(this._secondarySalesRate).val($('>SecondarySales>Rate', initialData).text()).change();
                    $(this._deprecated).prop('checked', ($('>Deprecated', initialData).text() == 'true' ? true : false)).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._dependencies).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    $(this._expand).html('Expand');
                    var that = this;
                    $('>Dependencies>Require', initialData).each(function() {
                        that.addDependency((new PCT.dependency()).setType('require').init($(this).text()));
                    });
                    $('>Dependencies>Exclude', initialData).each(function() {
                        that.addDependency((new PCT.dependency()).setType('exclude').init($(this).text()));
                    });
                    return this;
                },
                addDependency:function(dependency) {
                    if (dependency) {
                        dependency.setRoot(this._dependencies);
                        $('html, body').animate({
                            scrollTop: $(dependency.getHead()).offset().top
                        }, 200);
                    } else {
                        this.addDependency((new PCT.dependency()).setType('require'));
                    }
                }
            });
        },
        dependency:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("dependency");
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _key:$('#Key', dom).get(),
                _dependencyBody:$('#DependencyBody', dom).get(),
                _dependencyTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _type:$('#Type', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#DependencyBody', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    return $(that._key).val();
                }
            });
            $(this._remove).click(
                function() {
                    if (confirm('Remove dependency?')) {
                        $(that._body).remove();
                    }
                });
            $(this._key).change(function() {
                $(that._dependencyTitle).html($(this).val());
            });
            $(this._type).change(function() {
                if ($(this).val() == 'require') {
                    $(that._dependencyBody).addClass('requireDependency');
                    $(that._dependencyBody).removeClass('excludeDependency');
                } else {
                    $(that._dependencyBody).addClass('excludeDependency');
                    $(that._dependencyBody).removeClass('requireDependency');
                }
            });
            $(this._dependencyTitle).click(
                function() {
                    $(that._remove).toggleClass('hidden');
                    $(that._settingsPane).toggleClass('hidden');
                });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._key).val(initialData).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                },
                setType:function(type) {
                    $(this._type).val(type).change();
                    return this;
                }
            });
        },
        capacitiesGroup:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("capacitiesGroup");
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _content:$('#Content', dom).get(),
                _capacities:$('#Capacities', dom).get(),
                _groups:$('#Groups', dom).get(),
                _isRoot:$('#IsRoot', dom).get(),
                _groupTitle:$('#GroupTitle', dom).get(),
                _addGroup:$('#AddGroup', dom).get(),
                _addCapacity:$('#AddCapacity', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '';
                    if (!that.getIsRoot()) {
                        config += '<Group>';
                        config += '<Name>' + $(that._name).val() + '</Name>';
                        config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                    }
                    config += '<Capacities>';
                    $(that._capacities).children().each(function() {
                        if ($(this).hasClass('divCapacity'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    $(that._groups).children().each(function() {
                        if ($(this).hasClass('divCapacitiesGroup'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Capacities>';
                    if (!that.getIsRoot()) {
                        config += '</Group>';
                    }
                    return config;
                }
            });
            $(this._remove).click(
                function() {
                    if (confirm('Remove group?')) {
                        if (confirm('Group will be removed with nested capacities. Removing capacity can result in broken backward compatibility. Remove group?')) {
                            $(that._body).remove();
                        }
                    }
                });
            $(this._expand).click(
                function(arg) {
                    $(that._content).toggle(PCT.animation, function() {
                        if ($(this).css('display') == 'none') {
                            $(that._expand).html('Expand');
                        } else {
                            $(that._expand).html('Collapse');
                        }
                    });
                });
            $(this._name).change(function() {
                $(that._groupTitle).html($(this).val());
            });
            $(this._addGroup).click(function() {
                if ($(that._content).css('display') == 'none') {
                    $(that._content).show();
                    $(that._expand).html('Collapse');
                }
                that.addGroup();
            });
            $(this._addCapacity).click(function() {
                if ($(that._content).css('display') == 'none') {
                    $(that._content).show();
                    $(that._expand).html('Collapse');
                }
                that.addCapacity();
            });
            $(this._isRoot).change(function() {
                if ($(this).val() == 'true') {
                    $(that._name).val('CR').change();
                    $(that._content).addClass('hidden');
                    $(that._expand).html('Expand');
                    $(that._settings).hide();
                    $(that._groupTitle).html('Capacities Root');
                    $(that._settingsPane).addClass('hidden');
                    $(that._remove).addClass('hidden');
                    $(that._groupTitle).addClass('unclickable');
                } else {
                    $(that._groupTitle).click(
                        function() {
                            $(that._remove).toggleClass('hidden');
                            $(that._settingsPane).toggleClass('hidden');
                        });
                }
            });

            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                setIsRoot:function(r) {
                    $(this._isRoot).val(r ? 'true' : 'false').change();
                    return this;
                },
                getIsRoot:function() {
                    return $(this._isRoot).val() == 'true';
                },
                init:function(name, initialData) {
                    if (name) {
                        $(this._name).val(name).change();
                    }
                    var that = this;
                    $(this._shortName).val($('>ShortName', initialData).text()).change();
                    $('>Capacity', initialData).each(function() {
                        that.addCapacity((new PCT.capacity()).init(this));
                    });
                    $('>Group', initialData).each(function() {
                        that.addGroup((new PCT.capacitiesGroup()).init($('>Name', this).text(), $('>Capacities', this)).setIsRoot(false));
                    });
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                },
                addCapacity:function(capacity) {
                    if (capacity) {
                        capacity.setRoot(this._capacities);
                        $('html, body').animate({
                            scrollTop: $(capacity.getHead()).offset().top
                        }, 200);
                    } else {
                        this.addCapacity(new PCT.capacity());
                    }
                },
                addGroup:function(group) {
                    if (group) {
                        group.setRoot(this._groups);
                        $('html, body').animate({
                            scrollTop: $(group.getHead()).offset().top
                        }, 200);
                    } else {
                        this.addGroup((new PCT.capacitiesGroup()).setIsRoot(false));
                    }
                }
            });
        },
        capacity:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("capacity");
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _type:$('#Type', dom).get(),
                _key:$('#Key', dom).get(),
                _deprecated:$('#Deprecated', dom).get(),
                _capacityTitle:$('#Title', dom).get(),
                _addTier:$('#AddTier', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _tiers:$('#Tiers', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Capacity>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<Type>' + $(that._type).val() + '</Type>';
                    config += '<Deprecated>' + ($(that._deprecated).prop('checked') ? 'true' : 'false') + '</Deprecated>';
                    config += '<Tiers>';
                    $(that._tiers).children().each(function() {
                        if ($(this).hasClass('divTier'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Tiers>';
                    config += '</Capacity>';
                    return config;
                }
            });
            $(this._remove).click(
                function() {
                    if (confirm('Remove capacity?')) {
                        if (confirm('Removing capacity can result in broken backward compatibility. Remove capacity?')) {
                            $(that._body).remove();
                        }
                    }
                });
            $(this._name).change(function() {
                $(that._capacityTitle).html($(this).val());
            });
            $(this._expand).click(
                function(arg) {
                    $(that._tiers).toggle(PCT.animation, function() {
                        if ($(this).css('display') == 'none') {
                            $(that._expand).html('Expand');
                        } else {
                            $(that._expand).html('Collapse');
                        }
                    });
                });
            $(this._deprecated).change(function() {
                if ($(this).prop('checked')) {
                    $(that._capacityTitle).addClass('deprecated');
                } else {
                    $(that._capacityTitle).removeClass('deprecated');
                }
            });
            $(this._addTier).click(function() {
                if ($(that._tiers).css('display') == 'none') {
                    $(that._tiers).show();
                    $(that._expand).html('Collapse');
                }
                that.addTier();
            });
            $(this._capacityTitle).click(
                function() {
                    $(that._remove).toggleClass('hidden');
                    $(that._settingsPane).toggleClass('hidden');
                });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._shortName).val($('>ShortName', initialData).text()).change();
                    $(this._type).val($('>Type', initialData).text());
                    $(this._key).val($('>Key', initialData).text()).change();
                    $(this._deprecated).prop('checked', ($('>Deprecated', initialData).text() == 'true' ? true : false)).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._tiers).addClass('hidden');
                    $(this._expand).html('Expand');
                    $(this._remove).addClass('hidden');
                    var that = this;
                    $('>Tiers>Tier', initialData).each(function() {
                        that.addTier((new PCT.tier()).init(this));
                    });
                    return this;
                },
                addTier:function(tier) {
                    if (tier) {
                        tier.setRoot(this._tiers);
                        $('html, body').animate({
                            scrollTop: $(tier.getHead()).offset().top
                        }, 200);
                    } else {
                        this.addTier(new PCT.tier());
                    }
                }
            });
        },
        tier:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("tier");
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _bound:$('#Bound', dom).get(),
                _price:$('#Price', dom).get(),
                _tierTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Tier>';
                    config += '<Bound>' + $(that._bound).val() + '</Bound>';
                    config += '<Price>' + $(that._price).val() + '</Price>';
                    config += '</Tier>';
                    return config;
                }
            });
            $(this._remove).click(
                function() {
                    if (confirm('Remove tier?')) {
                        $(that._body).remove();
                    }
                });
            $(this._bound).change(function() {
                $(that._tierTitle).html($(this).val() + ' - ' + $(that._price).val());
            });
            $(this._price).change(function() {
                $(that._tierTitle).html($(that._bound).val() + ' - ' + $(this).val());
            });
            $(this._tierTitle).click(
                function() {
                    $(that._remove).toggleClass('hidden');
                    $(that._settingsPane).toggleClass('hidden');
                });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._bound).val($('>Bound', initialData).text()).change();
                    $(this._price).val($('>Price', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                }
            });
        }
    });
})(jQuery, window);