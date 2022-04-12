package com.kanastruk.mvi.ui

import injectors
import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.injector.inject
import org.w3c.dom.HTMLElement
import kotlin.properties.Delegates

data class Slide(
    val title:String,
    val description:String,
    val imgSrc:String,
    val imgStyle:String?=null,
    val imgLogoSrc:String?=null
)

class Carousel(slides: List<Slide> = emptyList()) {

    var foo :HTMLElement by Delegates.notNull()
    var bar :HTMLElement by Delegates.notNull()

    private val svgInjectors = injectors(
        Carousel::foo, Carousel::bar
    )

    // TODO: Injectors needed? maybe not.
    val header = document.create.inject(this,svgInjectors).div {
        id="carouselCaptions"
        classes += setOf("carousel", "slide")
        attributes["data-bs-ride"]="carousel"
        div("carousel-indicators") {
            button(type = ButtonType.button, classes = "active"){
                attributes["data-bs-target"]="#carouselCaptions"
                attributes["data-bs-slide-to"]="0"
            }
            button(type = ButtonType.button, classes = ""){
                attributes["data-bs-target"]="#carouselCaptions"
                attributes["data-bs-slide-to"]="1"
            }
        }
        div("carousel-inner") {
            slides.forEach { slide ->
                carouselSlide(slide,slides.first() == slide)
            }
        }
        button( classes="carousel-control-prev", type = ButtonType.button) {
            attributes["data-bs-target"]="#carouselCaptions"
            attributes["data-bs-slide"]="prev"
            span("carousel-control-prev-icon")
            span("visually-hidden") {+"Previous"}
        }
        button( classes="carousel-control-next", type = ButtonType.button) {
            attributes["data-bs-target"]="#carouselCaptions"
            attributes["data-bs-slide"]="next"
            span("carousel-control-next-icon")
            span("visually-hidden") {+"Next"}
        }
    }

    private fun DIV.carouselSlide(slide: Slide, active:Boolean=false) {
        div("carousel-item") {
            if(active) classes += "active"
            img(src=slide.imgSrc) {
                slide.imgStyle?.let { style = it }
            }
            div(classes = "carousel-caption") {
                if( slide.imgLogoSrc!=null ) {
                    classes += "text-center"
                    img(src = slide.imgLogoSrc, classes = "mb-3") { width = "15%" }
                    h2 { +slide.title }
                    p { +slide.description }

                } else {
                    classes += "text-center"
                    h2 { +slide.title }
                    p { +slide.description }
                }
            }
        }
    }

    // TODO: Fix this up, it doesn't work correctly for SVG, due to way it's added to the DOM?
    // https://github.com/Kotlin/kotlinx.html/wiki/Micro-templating-and-DSL-customizing
    // https://discuss.kotlinlang.org/t/dynamic-custom-attributes-of-tags-in-kotlinx-html-builders/7780
    class Rect(consumer: TagConsumer<*>) :
        HTMLTag("rect", consumer, emptyMap(), inlineTag = true, emptyTag = false),
        HtmlInlineTag

    fun SVG.rect(block: Rect.()->Unit={}) {
        Rect(consumer).visit(block)
    }
/*
        svg("bd-placeholder-img") {
            classes += injectorKey
            attributes["xmlns"]="http://www.w3.org/2000/svg"
            attributes["aria-hidden"]="true"
            attributes["width"] = "100%"
            attributes["height"] = "100%"
            attributes["preserveAspectRatio"] = "xMidYMid slice"
            rect {
//                attributes["style"] = "width:100%;height:100%;fill:#777"
                attributes["width"] = "100%"
                attributes["height"] = "100%"
                attributes["fill"] = "#777"
            }
        }
*/

    // https://stackoverflow.com/questions/13732326/jquery-created-svg-rect-doesnt-appear
    private fun DIV.placeholderSVG(injectorKey:String) {
        // img(src="...", classes="d-block w-100", alt = "...")
        img(src="images/ph777.svg") {  }
    }

}
