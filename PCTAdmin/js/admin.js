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
        format:'xml',
        template:function(template) {
            $.extend(this, {
                dom:$('<div></div>').append($(template).contents()),
                getCopy:function() {
                    return this.dom.contents().clone();
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
        }
    });
    $.extend(PCT, {
        init:function(templatesContainer, root) {
            $('template', templatesContainer).each(function() {
                PCT.addTemplate(this);
            });
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
                _products:$('#Products', dom).get()
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom),
                init:function(initialData) {
                    $(this._expiration).val($('>Expiration', initialData).text());
                    var that = this;
                    $('>Products>Product', initialData).each(function() {
                        that.addProduct((new PCT.product()).init(this));
                    });
                    return this;
                },
                addProduct:function(product) {
                    product.setRoot(this._products);
                }
            });
        },
        product:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("product");
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _name:$('#Name', dom).get(),
                _maximumFunctionalityPrice:$('#MaximumFunctionalityPrice', dom).get(),
                _minimumPrice:$('#MinimumPrice', dom).get(),
                _modules:$('#Modules', dom).get(),
                _productTitle:$('#ProductTitle', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _expand:$('#Expand', dom).get()
            });
            var that = this;
            $(this._expand).click(
                function() {
                    if ($(that._modules).hasClass('hidden')) {
                        $(that._expand).html('collapse');
                        $(that._modules).removeClass('hidden')
                    } else {
                        $(that._expand).html('expand');
                        $(that._modules).addClass('hidden')
                    }
                }).click();
            $(this._name).change(function() {
                $(that._productTitle).html($(this).val());
            });
            $(this._productTitle).addClass('underline').click(
                function() {
                    if ($(that._settingsPane).hasClass('hidden')) {
                        $(that._settingsPane).removeClass('hidden');
                    } else {
                        $(that._settingsPane).addClass('hidden');
                    }
                }).click();
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom),
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._maximumFunctionalityPrice).val($('>MaximumFunctionalityPrice', initialData).text());
                    $(this._minimumPrice).val($('>MinimumPrice', initialData).text());
                    this.addModulesRoot((new PCT.modulesGroup()).init(null, $('>Modules', initialData)));
                    return this;
                },
                addModulesRoot:function(modulesGroup) {
                    if (modulesGroup) {
                        modulesGroup.setRoot(this._modules);
                    } else {
                        this.addModulesRoot(new PCT.modulesGroup());
                    }
                }
            });
        },
        modulesGroup:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("modulesGroup");
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _name:$('#Name', dom).get(),
                _content:$('#Content', dom).get(),
                _modules:$('#Modules', dom).get(),
                _groups:$('#Groups', dom).get(),
                _isRoot:$('#IsRoot', dom).get(),
                _groupTitle:$('#GroupTitle', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _expand:$('#Expand', dom).get()
            });
            var that = this;
            $(this._expand).click(
                function() {
                    if ($(that._content).hasClass('hidden')) {
                        $(that._expand).html('collapse');
                        $(that._content).removeClass('hidden')
                    } else {
                        $(that._expand).html('expand');
                        $(that._content).addClass('hidden')
                    }
                }).click();
            $(this._name).change(function() {
                $(that._groupTitle).html($(this).val());
            });
            $(this._isRoot).change(function() {
                if ($(this).val() == 'true') {
                    $(that._settings).hide();
                    $(that._groupTitle).html('Modules Root');
                    $(that._settingsPane).addClass('hidden');
                } else {
                    $(that._groupTitle).addClass('underline').click(
                        function() {
                            if ($(that._settingsPane).hasClass('hidden')) {
                                $(that._settingsPane).removeClass('hidden');
                            } else {
                                $(that._settingsPane).addClass('hidden');
                            }
                        }).click();
                }
            });

            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom),
                init:function(name, initialData) {
                    if (name) {
                        $(this._name).val(name).change();
                        $(this._isRoot).val('false').change();
                    } else {
                        $(this._isRoot).val('true').change();
                    }
                    var that = this;
                    $('>Module', initialData).each(function() {
                        that.addModule((new PCT.module()).init(this));
                    });
                    $('>Group', initialData).each(function() {
                        that.addGroup((new PCT.modulesGroup()).init($('>Name', this).text(), $('>Modules', this)));
                    });
                    return this;
                },
                addModule:function(module) {
                    module.setRoot(this._modules);
                },
                addGroup:function(group) {
                    group.setRoot(this._groups);
                }
            });
        },
        module:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("module");
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _weight:$('#Weight', dom).get(),
                _key:$('#Key', dom).get(),
                _secondarySalesPrice:$('#SecondarySalesPrice', dom).get(),
                _secondarySalesRate:$('#SecondarySalesRate', dom).get(),
                _deprecated:$('#Deprecated', dom).get(),
                _moduleTitle:$('#ModuleTitle', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get()
            });
            var that = this;
            $(this._name).change(function() {
                $(that._moduleTitle).html($(this).val());
            });
            $(this._moduleTitle).addClass('underline').click(
                function() {
                    if ($(that._settingsPane).hasClass('hidden')) {
                        $(that._settingsPane).removeClass('hidden');
                    } else {
                        $(that._settingsPane).addClass('hidden');
                    }
                }).click();
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom),
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._shortName).val($('>ShortName', initialData).text());
                    $(this._weight).val($('>Weight', initialData).text());
                    $(this._key).val($('>Key', initialData).text());
                    $(this._secondarySalesPrice).val($('>SecondarySales>Price', initialData).text());
                    $(this._secondarySalesRate).val($('>SecondarySales>Rate', initialData).text());
                    $(this._deprecated).val($('>Deprecated', initialData).text());
                    return this;
                }
            });
        },
        moduleDependency:function(dom) {

        },
        capacitiesGroup:function(dom) {

        },
        capacity:function(dom) {

        }
    });
})(jQuery, window);