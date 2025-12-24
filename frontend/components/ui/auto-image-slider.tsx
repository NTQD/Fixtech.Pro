'use client'

import React, { useState, useEffect } from 'react'
import Image from 'next/image'
import { cn } from '@/lib/utils'

interface AutoImageSliderProps {
    images: string[]
    interval?: number
    className?: string
    imageClassName?: string
}

export function AutoImageSlider({
    images,
    interval = 2000,
    className,
    imageClassName
}: AutoImageSliderProps) {
    const [currentIndex, setCurrentIndex] = useState(0)

    useEffect(() => {
        if (images.length <= 1) return

        const timer = setInterval(() => {
            setCurrentIndex((prev) => (prev + 1) % images.length)
        }, interval)

        return () => clearInterval(timer)
    }, [images.length, interval])

    return (
        <div className={cn("relative w-full h-full overflow-hidden", className)}>
            {images.map((src, index) => (
                <div
                    key={src}
                    className={cn(
                        "absolute inset-0 w-full h-full transition-opacity duration-1000 ease-in-out",
                        index === currentIndex ? "opacity-100 z-10" : "opacity-0 z-0"
                    )}
                >
                    <Image
                        src={src}
                        alt={`Slide ${index + 1}`}
                        fill
                        className={cn("object-cover", imageClassName)}
                        priority={index === 0}
                        unoptimized
                    />
                </div>
            ))}
        </div>
    )
}
