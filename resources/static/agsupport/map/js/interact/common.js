// target elements with the "draggable" class

// interact('.el-dialog,.el-drawer')
interact('.el-dialog')
    .draggable({
        // enable inertial throwing
        inertia: true,
        // ignoreFrom:'.el-dialog__body,.dialog-footer,.el-tabs__nav',
        ignoreFrom:'.el-dialog__body',
        // keep the element within the area of it's parent
        modifiers: [
            interact.modifiers.restrictRect({
                restriction: 'parent',
                endOnly: true
            })
        ],
        // enable autoScroll
        autoScroll: true,

        // call this function on every dragmove event
        onmove: dragMoveListener,
        // call this function on every dragend event
        onend: function (event) {}
    }).resizable({
        // resize from all edges and corners
        edges: { left: true, right: true, bottom: true, top: true },
        modifiers: [
            // keep the edges inside the parent
            interact.modifiers.restrictEdges({
                outer: 'parent',
                endOnly: true
            }),
            // minimum size
            interact.modifiers.restrictSize({
                min: { width: 200, height: 150 }
            })
        ],

        inertia: true
    }).on('resizemove', function (event) {
        resizemove(event);
    });

// interact('.right-sidebar')
//     .draggable({
//         // enable inertial throwing
//         inertia: true,
//         ignoreFrom:'.right-sidebar-content,.right-sidebar-footer',
//         // keep the element within the area of it's parent
//         modifiers: [
//             interact.modifiers.restrictRect({
//                 restriction: 'parent',
//                 endOnly: true
//             })
//         ],
//         // enable autoScroll
//         autoScroll: true,
//
//         // call this function on every dragmove event
//         onmove: function (event) {},
//         // call this function on every dragend event
//         onend: function (event) {}
//     }).resizable({
//     // resize from all edges and corners
//     edges: {left: true,  bottom: true, top: true },
//     modifiers: [
//         // keep the edges inside the parent
//         interact.modifiers.restrictEdges({
//             outer: 'parent',
//             endOnly: true
//         }),
//         // minimum size
//         interact.modifiers.restrictSize({
//             min: { width: 200, height: 150 }
//         })
//     ],
//
//     inertia: true
// }).on('resizemove', function (event) {
//     resizemove(event);
// });

function dragMoveListener (event) {
    var target = event.target
    // keep the dragged position in the data-x/data-y attributes
    var x = (parseFloat(target.getAttribute('data-x')) || 0) + event.dx
    var y = (parseFloat(target.getAttribute('data-y')) || 0) + event.dy

    // translate the element
    target.style.webkitTransform =
        target.style.transform =
            'translate(' + x + 'px, ' + y + 'px)'
    // update the posiion attributes
    target.setAttribute('data-x', x)
    target.setAttribute('data-y', y)
}

function resizemove(event){
    var target = event.target
    // console.log();
    var x = (parseFloat(target.getAttribute('data-x')) || 0)
    var y = (parseFloat(target.getAttribute('data-y')) || 0)

    // update the element's style
    target.style.width = event.rect.width + 'px'
    target.style.height = event.rect.height + 'px'

    // translate when resizing from top or left edges
    x += event.deltaRect.left
    y += event.deltaRect.top

    target.style.webkitTransform = target.style.transform =
        'translate(' + x + 'px,' + y + 'px)'

    target.setAttribute('data-x', x)
    target.setAttribute('data-y', y)
    if($(target).children(".el-dialog__body")){
        var hh=event.rect.height -82;
        if($(target).children(".el-dialog__footer")){
            hh=hh-32;
        }
        $(target).children(".el-dialog__body").css("height",hh+ 'px');
        $(target).children(".el-dialog__body").css("overflow",'auto');
    }else if($(target).children(".right-sidebar-content")){
        var hh=event.rect.height -82;
        if($(target).children(".right-sidebar-footer")){
            hh=hh-32;
        }
        // $(target).children(".el-tabs__content").css("height",hh+ 'px');
        $(target).children(".el-tabs__content").css("overflow",'auto');
    }
}

// this is used later in the resizing and gesture demos
window.dragMoveListener = dragMoveListener




