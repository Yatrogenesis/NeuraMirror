package com.neuramirror.util

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

/**
 * LiveData que sólo notifica a los observadores una vez.
 * 
 * Útil para eventos como navegación o mensajes de Snackbar que sólo deben ejecutarse una vez.
 */
class SingleLiveEvent<T> : MutableLiveData<T>() {
    
    private val mPending = AtomicBoolean(false)
    
    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Timber.w("Múltiples observadores registrados pero solo uno será notificado")
        }
        
        // Observar el evento interno
        super.observe(owner) { t ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }
    
    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }
    
    /**
     * Utilidad para disparar el evento de forma manual
     */
    @MainThread
    fun call() {
        setValue(null)
    }
}
