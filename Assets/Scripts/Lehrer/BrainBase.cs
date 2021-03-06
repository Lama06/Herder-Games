using System;
using HerderGames.Lehrer.AI;
using HerderGames.Lehrer.Fragen;
using UnityEngine;

namespace HerderGames.Lehrer
{
    [RequireComponent(typeof(Lehrer))]
    public abstract class BrainBase : MonoBehaviour
    {
        protected Lehrer Lehrer { get; private set; }

        protected virtual void Awake()
        {
            Lehrer = GetComponent<Lehrer>();
        }

        protected virtual void Start()
        {
            RegisterGoals(Lehrer.AI);
            RegisterFragen(Lehrer.FragenManager);
        }

        protected abstract void RegisterGoals(AIController ai);

        protected abstract void RegisterFragen(InteraktionsMenuFragenManager fragen);
    }
}