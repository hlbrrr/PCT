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
        disableAnimation:function() {
            $.fx.off = true;
        },
        animation:'blind',
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
            PCT.disableAnimation();
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
                _products:$('#Products', dom).get(),
                _addProduct:$('#AddProduct', dom).get()
            });
            var that = this;
            $(this._addProduct).click(function() {
                that.addProduct();
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                init:function(initialData) {
                    $(this._expiration).datepicker({
                        dateFormat:'dd/mm/yy',
                        minDate:new Date(),
                        showAnim: PCT.animation
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
                _maximumFunctionalityPrice:$('#MaximumFunctionalityPrice', dom).get(),
                _minimumPrice:$('#MinimumPrice', dom).get(),
                _content:$('#Content', dom).get(),
                _modules:$('#Modules', dom).get(),
                _capacities:$('#Capacities', dom).get(),
                _productTitle:$('#ProductTitle', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get()
            });
            var that = this;
            $(this._remove).click(
                function() {
                    $(that._body).remove();
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
                    $(that._remove).toggle(PCT.animation);
                    $(that._settingsPane).toggle(PCT.animation);
                });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._maximumFunctionalityPrice).val($('>MaximumFunctionalityPrice', initialData).text());
                    $(this._minimumPrice).val($('>MinimumPrice', initialData).text());
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
                        modulesGroup.setRoot(this._modules);
                    } else {
                        this.addModulesRoot((new PCT.modulesGroup()).setIsRoot(true));
                    }
                    return this;
                },
                addCapacitiesRoot:function(capacitiesGroup) {
                    if (capacitiesGroup) {
                        capacitiesGroup.setRoot(this._capacities);
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
                _remove:$('#Remove', dom).get()
            });
            var that = this;
            $(this._remove).click(
                function() {
                    $(that._body).remove();
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
                            $(that._remove).toggle(PCT.animation);
                            $(that._settingsPane).toggle(PCT.animation);
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
                _remove:$('#Remove', dom).get()
            });
            var that = this;
            $(this._remove).click(
                function() {
                    $(that._body).remove();
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
                    $(that._remove).toggle(PCT.animation);
                    $(that._settingsPane).toggle(PCT.animation);
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
                    $(this._shortName).val($('>ShortName', initialData).text());
                    $(this._weight).val($('>Weight', initialData).text());
                    $(this._key).val($('>Key', initialData).text());
                    $(this._secondarySalesPrice).val($('>SecondarySales>Price', initialData).text());
                    $(this._secondarySalesRate).val($('>SecondarySales>Rate', initialData).text());
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
                _remove:$('#Remove', dom).get()
            });
            var that = this;
            $(this._remove).click(
                function() {
                    $(that._body).remove();
                });
            $(this._key).change(function() {
                $(that._dependencyTitle).html($(this).val());
            });
            $(this._type).change(function() {
                if($(this).val()=='require'){
                   $(that._dependencyBody).addClass('requireDependency');
                   $(that._dependencyBody).removeClass('excludeDependency');
                }else{
                   $(that._dependencyBody).addClass('excludeDependency');
                   $(that._dependencyBody).removeClass('requireDependency');
                }
            });
            $(this._dependencyTitle).click(
                function() {
                    $(that._remove).toggle(PCT.animation);
                    $(that._settingsPane).toggle(PCT.animation);
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
                setType:function(type){
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
                _remove:$('#Remove', dom).get()
            });
            var that = this;
            $(this._remove).click(
                function() {
                    $(that._body).remove();
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
                            $(that._remove).toggle(PCT.animation);
                            $(that._settingsPane).toggle(PCT.animation);
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
                init:function(name, initialData) {
                    if (name) {
                        $(this._name).val(name).change();
                        $(this._isRoot).val('false').change();
                    } else {
                        $(this._isRoot).val('true').change();
                    }
                    var that = this;
                    $('>Capacity', initialData).each(function() {
                        that.addCapacity((new PCT.capacity()).init(this));
                    });
                    $('>Group', initialData).each(function() {
                        that.addGroup((new PCT.capacitiesGroup()).init($('>Name', this).text(), $('>Capacities', this)));
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
                _type:$('#Type', dom).get(),
                _key:$('#Key', dom).get(),
                _deprecated:$('#Deprecated', dom).get(),
                _capacityTitle:$('#Title', dom).get(),
                _addTier:$('#AddTier', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _tiers:$('#Tiers', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get()
            });
            var that = this;
            $(this._remove).click(
                function() {
                    $(that._body).remove();
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
                    $(that._remove).toggle(PCT.animation);
                    $(that._settingsPane).toggle(PCT.animation);
                });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._type).val($('>Type', initialData).text());
                    $(this._key).val($('>Key', initialData).text());
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
                _remove:$('#Remove', dom).get()
            });
            var that = this;
            $(this._remove).click(
                function() {
                    $(that._body).remove();
                });
            $(this._bound).change(function() {
                $(that._tierTitle).html($(this).val() + ' - ' + $(that._price).val());
            });
            $(this._price).change(function() {
                $(that._tierTitle).html($(that._bound).val() + ' - ' + $(this).val());
            });
            $(this._tierTitle).click(
                function() {
                    $(that._remove).toggle(PCT.animation);
                    $(that._settingsPane).toggle(PCT.animation);
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