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
                root.append(this.root.contents());
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
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom),
                init:function(initialData) {
                    $('#Expiration', this.getRoot()).val($('>Expiration', initialData).text());
                    var that = this;
                    $('>Products>Product', initialData).each(function() {
                        that.addProduct((new PCT.product()).init(this));
                    });
                    return this;
                },
                addProduct:function(product) {
                    product.setRoot($('#Products', this.getRoot()));
                }
            });
        },
        product:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate("product");
            }
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom),
                init:function(initialData) {
                    $('#Name', this.getRoot()).val($('>Name', initialData).text());
                    $('#MaximumFunctionalityPrice', this.getRoot()).val($('>MaximumFunctionalityPrice', initialData).text());
                    $('#MinimumPrice', this.getRoot()).val($('>MinimumPrice', initialData).text());
                    return this;
                }
            });
        },
        modulesGroup:function(dom) {

        },
        module:function(dom, root) {
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom),
                init:function(initialData) {
                    $('#Name', this.getRoot()).val($('>Name', initialData).text());
                    $('#ShortName', this.getRoot()).val($('>ShortName', initialData).text());
                    $('#Weight', this.getRoot()).val($('>Weight', initialData).text());
                    $('#Key', this.getRoot()).val($('>Key', initialData).text());
                    $('#SecondarySalesPrice', this.getRoot()).val($('>SecondarySales>Price', initialData).text());
                    $('#SecondarySalesRate', this.getRoot()).val($('>SecondarySales>Rate', initialData).text());
                    $('#Deprecated', this.getRoot()).val($('>Deprecated', initialData).text());
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